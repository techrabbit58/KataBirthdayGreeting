package de.pruefbit.kata;

import com.sun.istack.internal.NotNull;

import java.util.Properties;

class GreetingContext {

    FriendsDirectory getDatabase() {
        return database;
    }

    NotificationService getNotificationService() {
        return notificationService;
    }

    Properties getProperties() {
        return properties;
    }

    private final FriendsDirectory database;
    private final NotificationService notificationService;
    private final Properties properties = new Properties();
    static private final Properties defaultProperties = new Properties();
    static {
        defaultProperties.setProperty("greeting_template", "Subject: Happy birthday!\n\nHappy birthday, dear {0}!");
        defaultProperties.setProperty("address", "email");
        defaultProperties.setProperty("firstName", "first_name");
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
        private Properties properties;

        Builder setDatabase(@NotNull FriendsDirectory db) {
            this.database = db;
            return this;
        }

        Builder setNotificationService(@NotNull NotificationService notificationService) {
            this.notificationService = notificationService;
            return this;
        }

        Builder setProperties(@NotNull Properties properties) {
            this.properties = properties;
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
