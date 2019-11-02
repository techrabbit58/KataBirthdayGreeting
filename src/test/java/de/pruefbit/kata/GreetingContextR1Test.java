package de.pruefbit.kata;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static de.pruefbit.kata.PropertyNames.*;
import static org.junit.jupiter.api.Assertions.*;

class GreetingContextR1Test {

    @Test
    void context_build_throws_RuntimeException_without_mandatory_settings() {
        assertThrows(RuntimeException.class, () -> new GreetingContext.Builder().build());
    }

    @Test
    void getProperties_works_with_all_default_properties() {
        GreetingContext context = new GreetingContext.Builder()
                .setDatabase(new MockDatabase())
                .setNotificationService(new MockNotificationService())
                .build();
        String expected = "Subject: Happy birthday!\n\nHappy birthday, dear {0}!";
        String actual = context.getProperties().getProperty(GREETING_TEMPLATE.text);
        assertEquals(expected, actual);
    }

    private static class MockDatabase implements FriendsDirectory {

        @Override
        public List<Friend> selectByDate(LocalDate date) {
            return null;
        }
    }

    private static class MockNotificationService implements NotificationService {

        @Override
        public void sendGreeting(String address, String message) {}
    }
}