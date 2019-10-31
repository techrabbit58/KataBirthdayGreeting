package de.pruefbit.kata;

import com.sun.istack.internal.NotNull;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeAll;
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
    private static Map<String, String> notifications = new HashMap<>();

    @Test
    void BirthdayGreeting_for_common_date_with_person_to_greet() {
        BirthdayGreeting greeter = new BirthdayGreeting(friends, notificationService);
        greeter.overrideToday(LocalDate.of(1999, 6, 3));
        greeter.run();
        notifications.forEach((k, v) -> System.out.println("mailto:" + k + "\n" + v));
    }

    @BeforeAll
    static void setUpAny() {
        friends = new MockFriendsDatabase(
                String.join("\n",
                        "last_name, first_name, date_of_birth, email",
                        "Doe, John, 1982/10/08, john.doe@foobar.com",
                        "Ann, Mary, 1975/09/11, mary.ann@foobar.com",
                        "Reacher, Jack, 1958/10/31, jacko@nowhere.org",
                        "Ford, Francis, 1947/02/28, fran@example.com",
                        "Fender, Frank, 1996/02/29, frank.d.fender1@acme.biz",
                        "Kowalsky, Nick, 1979/06/03, n.kowalsky@example.com"
                )
        );
        notificationService = new MockNotificationService(notifications);
    }

    private static class MockFriendsDatabase implements FriendsDirectory {
        private Iterable<CSVRecord> csv;

        MockFriendsDatabase(String rawInput) {
            try {
                this.csv = CSVFormat.RFC4180
                        .withFirstRecordAsHeader()
                        .withIgnoreSurroundingSpaces()
                        .parse(new StringReader(rawInput));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public List<Friend> selectByDate(LocalDate date) {
            List<String> daySelection = new ArrayList<>();
            daySelection.add(date.format(DateTimeFormatter.ofPattern("MM/dd")));
            if (!date.isLeapYear() && date.equals(LocalDate.of(date.getYear(), Month.FEBRUARY, 28))) {
                MonthDay d = MonthDay.of(2, 29);
                daySelection.add(d.format(DateTimeFormatter.ofPattern("MM/dd")));
            }
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

        private final Map<String, String> notifications;

        MockNotificationService(@NotNull Map<String, String> notifications) {
            this.notifications = notifications;
        }

        @Override
        public void send(String email, String message) {
            notifications.put(email, message);
        }
    }
}