package de.pruefbit.kata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BirthdayGreetingTest {

    @Test
    void BirthdayGreeting_construction() {
        FriendsDirectory friends = new MockFriendsDatabase();
        NotificationService notificationService = new MockNotificationService();
        BirthdayGreeting greeter = new BirthdayGreeting(friends, notificationService);
        assertNotNull(greeter);
    }

    private class MockFriendsDatabase implements FriendsDirectory {
    }

    private class MockNotificationService implements NotificationService {
    }
}