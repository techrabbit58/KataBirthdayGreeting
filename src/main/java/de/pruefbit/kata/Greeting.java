package de.pruefbit.kata;

import com.sun.istack.internal.NotNull;

class Greeting {

    private final String template;
    private String name;

    Greeting() {
        template = "Subject: Happy birthday!\n\nHappy birthday, dear %s!";
    }

    Greeting to(@NotNull String name) {
        this.name = name;
        return this;
    }

    String build() {
        return String.format(template, name);
    }
}
