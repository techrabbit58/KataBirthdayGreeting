package de.pruefbit.kata;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
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

    void overrideTodayWithDate(@NotNull LocalDate date) {
        this.today = date;
    }

    @Override
    public void run() {
        List<Friend> friendsToGreet;
        try {
            friendsToGreet = friendsDirectory.selectByDate(today);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        friendsToGreet.forEach(f -> {
            String message = new Greeting("%s").to(f.get("first_name")).build();
            notificationService.sendGreeting(f.get("email"), message);
        });
    }
}
