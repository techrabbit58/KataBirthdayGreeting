package de.pruefbit.kata;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
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

import static org.junit.jupiter.api.Assertions.*;

class BirthdayGreetingTest {
    private static MockFriendsDatabase friends;
    private static NotificationService notificationService;
    private static final Map<String, String> notifications = new HashMap<>();
    private static List<CSVRecord> csv;

    private static final List<String> FRIENDS_DIRECTORY = Arrays.asList(
            "last_name, first_name, date_of_birth, email",
            "Doe, John, 1982/10/08, john.doe@foobar.com",
            "Ann, Mary, 1975/09/11, mary.ann@foobar.com",
            "Reacher, Jack, 1958/10/31, jacko@nowhere.org",
            "Ford, Francis, 1947/02/28, fran@example.com",
            "Fender, Frank, 1996/02/29, frank.d.fender1@acme.biz",
            "Woodpecker, Cornelia, 1956/09/11, connyw@example.com",
            "Kowalsky, Nick, 1979/06/03, n.kowalsky@example.com"
    );

    @BeforeAll
    static void setUpAny() {
        friends = new MockFriendsDatabase();
        notificationService = new MockNotificationService();
    }

    @BeforeEach
    void setUpEach() {
        notifications.clear();
    }

    @Test
    void BirthdayGreeting_for_common_date_with_one_person_to_greet() {
        BirthdayGreeting greeter = new BirthdayGreeting(friends, notificationService);
        LocalDate date = LocalDate.of(1999, 6, 3);
        assertTrue(directoryHasMatchForDate(date));
        greeter.overrideTodayWithDate(date);
        greeter.run();
        assertEquals(1, notifications.size());
        notifications.forEach((address, firstName) -> assertTrue(directoryHasMatchForAddressAndFirstName(address, firstName)));
    }

    @Test
    void BirthdayGreeting_for_common_date_with_more_persons_to_greet() {
        BirthdayGreeting greeter = new BirthdayGreeting(friends, notificationService);
        LocalDate date = LocalDate.of(1999, 9, 11);
        assertTrue(directoryHasMatchForDate(date));
        greeter.overrideTodayWithDate(date);
        greeter.run();
        assertTrue(notifications.size() > 1);
        notifications.forEach((address, firstName) -> assertTrue(directoryHasMatchForAddressAndFirstName(address, firstName)));
    }

    @Test
    void BirthdayGreeting_for_common_date_without_persons_to_greet() {
        BirthdayGreeting greeter = new BirthdayGreeting(friends, notificationService);
        LocalDate date = LocalDate.of(1999, 6, 4);
        assertFalse(directoryHasMatchForDate(date));
        greeter.overrideTodayWithDate(date);
        greeter.run();
        assertEquals(0, notifications.size());
    }

    private boolean directoryHasMatchForDate(LocalDate date) {
        return FRIENDS_DIRECTORY.stream().anyMatch(s -> s.contains(date.format(DateTimeFormatter.ofPattern("MM/dd,"))));
    }

    private boolean directoryHasMatchForAddressAndFirstName(String address, String firstName) {
        return FRIENDS_DIRECTORY.stream().anyMatch(s -> s.contains(firstName + ',') && s.contains(address));
    }

    private static class MockFriendsDatabase implements FriendsDirectory {

        MockFriendsDatabase() {
            csv = parseTextAsCsv(String.join("\n", FRIENDS_DIRECTORY));
        }

        private List<CSVRecord> parseTextAsCsv(String rawInput) {
            try {
                return CSVFormat.RFC4180
                        .withSkipHeaderRecord()
                        .withHeader("last_name", "first_name", "date_of_birth", "email")
                        .withIgnoreSurroundingSpaces()
                        .parse(new StringReader(rawInput))
                        .getRecords();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            return null;
        }

        @Override
        public List<Friend> selectByDate(LocalDate date) {
            List<String> daySelection = collectSelectionCriteria(date);
            return collectGreetingCandidates(daySelection);
        }

        private List<String> collectSelectionCriteria(LocalDate date) {
            List<String> daySelection = new ArrayList<>();
            daySelection.add(date.format(DateTimeFormatter.ofPattern("MM/dd")));
            if (!date.isLeapYear() && date.equals(LocalDate.of(date.getYear(), Month.FEBRUARY, 28))) {
                MonthDay d = MonthDay.of(2, 29);
                daySelection.add(d.format(DateTimeFormatter.ofPattern("MM/dd")));
            }
            return daySelection;
        }

        private List<Friend> collectGreetingCandidates(List<String> daySelection) {
            List<Friend> friendsToGreet = new ArrayList<>();
            csv.forEach(r -> {
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
        public void sendGreeting(String address, String message) {
            notifications.put(address, message);
        }
    }
}