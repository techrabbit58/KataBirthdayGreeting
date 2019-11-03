package de.pruefbit.birthdaygreeting;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static de.pruefbit.birthdaygreeting.PropertyNames.*;
import static org.junit.jupiter.api.Assertions.*;

class BirthdayGreetingR1Test {
    private static NotificationService notificationService;
    private static CSVParser csv;
    private static final Map<String, String> notifications = new HashMap<>();
    private static final String todaySelector = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd"));
    private static final List<String> FRIENDS_DIRECTORY = Arrays.asList(
            "last_name, first_name, date_of_birth, email",
            "Doe, John, 1982/10/08, john.doe@foobar.com",
            "Ann, Mary, 1975/09/11, mary.ann@foobar.com",
            "Reacher, Jack, 1958/10/31, jacko@nowhere.org",
            "Ford, Francis, 1947/02/28, fran@example.com",
            "Fender, Frank, 1996/02/29, frank.d.fender1@acme.biz",
            "Woodpecker, Cornelia, 1956/09/11, connyw@example.com",
            "Kowalsky, Nick, 1979/06/03, n.kowalsky@example.com",
            "Woodpecker, Woody, 1955/" + todaySelector + ", woodyw@example.com"
    );

    private static final Properties properties = new Properties();
    static {
        properties.put(GREETING_TEMPLATE.text, "{0}");
    }

    private static GreetingContext context;

    @BeforeAll
    static void setUpAny() {
        MockFriendsDatabase friends = new MockFriendsDatabase();
        notificationService = new MockNotificationService();
        context = new GreetingContext.Builder()
                .setDatabase(friends)
                .setNotificationService(notificationService)
                .setProperties(properties)
                .build();
    }

    @BeforeEach
    void setUpEach() throws IOException {
        csv = parseTextAsCsv(String.join("\n", FRIENDS_DIRECTORY));
        notifications.clear();
    }

    private CSVParser parseTextAsCsv(String rawInput) throws IOException {
        return CSVFormat.RFC4180
                .withFirstRecordAsHeader()
                .withIgnoreSurroundingSpaces()
                .parse(new StringReader(rawInput));
    }

    @Test
    void BirthdayGreeting_for_common_date_with_one_person_to_greet() {
        BirthdayGreeting greeter = new BirthdayGreeting(context);
        LocalDate date = LocalDate.of(1999, 6, 3);
        assertTrue(directoryHasMatchForDate(date));
        greeter.overrideTodayWithDate(date);
        greeter.run();
        assertEquals(1, notifications.size());
        notifications.forEach((address, firstName) -> assertTrue(
                directoryHasMatchForAddressAndFirstName(address, firstName)));
    }

    @Test
    void BirthdayGreeting_works_for_current_date() {
        BirthdayGreeting greeter = new BirthdayGreeting(context);
        assertTrue(directoryHasMatchForDate(LocalDate.now()));
        greeter.run();
        assertTrue(notifications.size() >= 1);
        notifications.forEach((address, firstName) -> assertTrue(
                directoryHasMatchForAddressAndFirstName(address, firstName)));
    }

    @Test
    void BirthdayGreeting_for_common_date_with_more_persons_to_greet() {
        BirthdayGreeting greeter = new BirthdayGreeting(context);
        LocalDate date = LocalDate.of(1999, 9, 11);
        assertTrue(directoryHasMatchForDate(date));
        greeter.overrideTodayWithDate(date);
        greeter.run();
        assertTrue(notifications.size() > 1);
        notifications.forEach((address, firstName) -> assertTrue(
                directoryHasMatchForAddressAndFirstName(address, firstName)));
    }

    @Test
    void BirthdayGreeting_for_common_date_without_persons_to_greet() {
        BirthdayGreeting greeter = new BirthdayGreeting(context);
        LocalDate date = LocalDate.of(1999, 6, 4);
        assertFalse(directoryHasMatchForDate(date));
        greeter.overrideTodayWithDate(date);
        greeter.run();
        assertEquals(0, notifications.size());
    }

    @Test
    void BirthdayGreeting_for_Feb_29_works_with_leap_years() {
        BirthdayGreeting greeter = new BirthdayGreeting(context);
        LocalDate date = LocalDate.of(2004, 2, 29);
        assertTrue(directoryHasMatchForDate(date));
        assertTrue(date.isLeapYear());
        greeter.overrideTodayWithDate(date);
        greeter.run();
        assertEquals(1, notifications.size());
        notifications.forEach((address, firstName) -> assertTrue(
                directoryHasMatchForAddressAndFirstName(address, firstName)));
    }

    @Test
    void BirthdayGreeting_for_Feb_29_works_outside_leap_years() {
        BirthdayGreeting greeter = new BirthdayGreeting(context);
        LocalDate date = LocalDate.of(2003, 2, 28);
        assertFalse(date.isLeapYear());
        greeter.overrideTodayWithDate(date);
        greeter.run();
        assertTrue(notifications.size() > 1);
    }

    @Test
    void BirthdayGreeting_throws_RuntimeException_on_IO_error() {
        GreetingContext context = new GreetingContext.Builder()
                .setDatabase(new MockFaultyDatabase())
                .setNotificationService(notificationService)
                .setProperties(properties)
                .build();
        BirthdayGreeting greeter = new BirthdayGreeting(context);
        assertThrows(RuntimeException.class, greeter::run);
    }

    private boolean directoryHasMatchForDate(LocalDate date) {
        return FRIENDS_DIRECTORY.stream().anyMatch(s -> s.contains(date.format(DateTimeFormatter.ofPattern("MM/dd,"))));
    }

    private boolean directoryHasMatchForAddressAndFirstName(String address, String firstName) {
        return FRIENDS_DIRECTORY.stream().anyMatch(s -> s.contains(firstName + ',') && s.contains(address));
    }

    private static class MockFriendsDatabase implements FriendsDirectory {

        @Override
        public List<Friend> selectByDate(LocalDate date) throws IOException {
            List<String> daySelection = prepareSelectionCriteria(date);
            return collectGreetingCandidates(daySelection);
        }

        private List<String> prepareSelectionCriteria(LocalDate date) {
            List<String> daySelection = new ArrayList<>();
            daySelection.add(date.format(DateTimeFormatter.ofPattern("MM/dd")));
            if (!date.isLeapYear() && date.equals(LocalDate.of(date.getYear(), Month.FEBRUARY, 28))) {
                MonthDay d = MonthDay.of(2, 29);
                daySelection.add(d.format(DateTimeFormatter.ofPattern("MM/dd")));
            }
            return daySelection;
        }

        private List<Friend> collectGreetingCandidates(List<String> daySelection) throws IOException {
            List<Friend> friendsToGreet = new ArrayList<>();
            csv.getRecords().forEach(r -> {
                for (String day : daySelection) {
                    if (r.get("date_of_birth").endsWith(day)) {
                        friendsToGreet.add(Friend.fromMap(r.toMap()));
                    }
                }
            });
            return friendsToGreet;
        }
    }

    private static class MockNotificationService implements NotificationService {

        @Override
        public void send(String address, String message) {
            notifications.put(address, message);
        }
    }

    private static class MockFaultyDatabase implements FriendsDirectory {

        @Override
        public List<Friend> selectByDate(LocalDate date) throws IOException {
            throw new IOException("faulty database");
        }
    }
}