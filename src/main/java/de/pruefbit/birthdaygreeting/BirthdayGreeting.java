package de.pruefbit.birthdaygreeting;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static de.pruefbit.birthdaygreeting.PropertyNames.*;

class BirthdayGreeting implements Runnable {
    private final FriendsDirectory friendsDirectory;
    private final NotificationService notificationService;
    private final Properties properties;
    private LocalDate today;

    BirthdayGreeting(GreetingContext context) {
        friendsDirectory = context.getDatabase();
        notificationService = context.getNotificationService();
        properties = context.getProperties();
        today = LocalDate.now();
    }

    void overrideTodayWithDate(LocalDate date) {
        this.today = Objects.requireNonNull(date);
    }

    @Override
    public void run() {
        List<Friend> friendsToGreet;
        try {
            friendsToGreet = friendsDirectory.selectByDate(today);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        friendsToGreet.forEach(ftg -> {
            String message = new Greeting(properties.getProperty(GREETING_TEMPLATE.text))
                    .toName(ftg.get(properties.getProperty(FIRST_NAME.text)))
                    .build();
            notificationService.send(ftg.get(properties.getProperty(ADDRESS.text)), message);
        });
    }
}
