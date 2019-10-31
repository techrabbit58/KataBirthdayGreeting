package de.pruefbit.kata;

import com.sun.istack.internal.NotNull;

import java.time.LocalDate;
import java.util.List;

class BirthdayGreeting implements Runnable {
    private final FriendsDirectory friendsDirectory;
    private final NotificationService notificationService;
    private LocalDate today;

    BirthdayGreeting(@NotNull FriendsDirectory friendsDirectory, @NotNull NotificationService notificationService) {
        this.friendsDirectory = friendsDirectory;
        this.notificationService = notificationService;
        this.today = LocalDate.now();
    }

    void overrideToday(@NotNull LocalDate date) {
        this.today = date;
    }

    @Override
    public void run() {
        List<Friend> friends = friendsDirectory.selectByDate(today);
        friends.forEach(f -> {
            String message = new Greeting().to(f.getFirstName()).build();
            notificationService.send(f.getEmail(), message);
        });
    }
}
