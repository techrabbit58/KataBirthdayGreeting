package de.pruefbit.kata;

import com.sun.istack.internal.NotNull;

class BirthdayGreeting {
    private final FriendsDirectory friendsDirectory;
    private final NotificationService notificationService;

    BirthdayGreeting(@NotNull FriendsDirectory friendsDirectory, @NotNull NotificationService notificationService) {
        this.friendsDirectory = friendsDirectory;
        this.notificationService = notificationService;
    }
}
