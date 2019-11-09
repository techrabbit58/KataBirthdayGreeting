package de.pruefbit.birthdaygreeting;

import java.util.Properties;

import static de.pruefbit.birthdaygreeting.PropertyNames.*;

class GreetingContext {
    private final FriendsDirectory database;
    private final NotificationService notificationService;
    private final Properties properties = new Properties();
    static private final Properties defaultProperties = new Properties();
    static {
        defaultProperties.setProperty(GREETING_TEMPLATE.text, "Subject: Happy birthday!\n\nHappy birthday, dear {0}!");
        defaultProperties.setProperty(ADDRESS.text, "email");
        defaultProperties.setProperty(FIRST_NAME.text, "first_name");
        defaultProperties.setProperty(LAST_NAME.text, "last_name");
    }

    FriendsDirectory getDatabase() {
        return database;
    }

    NotificationService getNotificationService() {
        return notificationService;
    }

    Properties getProperties() {
        return properties;
    }

    private GreetingContext(
            FriendsDirectory database,
            NotificationService notificationService,
            Properties moreProperties) {
        this.database = database;
        this.notificationService = notificationService;
        properties.putAll(defaultProperties);
        properties.putAll(moreProperties);
    }

    static class Builder {
        private FriendsDirectory database;
        private NotificationService notificationService;
        private final Properties properties = new Properties();

        Builder setDatabase(FriendsDirectory db) {
            this.database = db;
            return this;
        }

        Builder setNotificationService(NotificationService notificationService) {
            this.notificationService = notificationService;
            return this;
        }

        Builder setProperties(Properties moreProperties) {
            properties.putAll(moreProperties);
            return this;
        }

        GreetingContext build() {
            if (database == null && notificationService == null) {
                throw new RuntimeException("unknown database and/or notification service");
            }
            return new GreetingContext(database, notificationService, properties);
        }
    }
}
