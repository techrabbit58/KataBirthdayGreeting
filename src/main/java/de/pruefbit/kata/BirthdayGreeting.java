package de.pruefbit.kata;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import static de.pruefbit.kata.PropertyNames.*;

class BirthdayGreeting implements Runnable {
    private final FriendsDirectory friendsDirectory;
    private final NotificationService notificationService;
    private final Properties properties;
    private LocalDate today;

    BirthdayGreeting(GreetingContext context) {
        friendsDirectory = context.getDatabase();
        notificationService = context.getNotificationService();
        properties = context.getProperties();
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
            throw new RuntimeException(e.getMessage());
        }
        friendsToGreet.forEach(f -> {
            String message = new Greeting(properties.getProperty(GREETING_TEMPLATE.text))
                    .to(f.get(properties.getProperty(FIRST_NAME.text)))
                    .build();
            notificationService.sendGreeting(f.get(properties.getProperty(ADDRESS.text)), message);
        });
    }
}
