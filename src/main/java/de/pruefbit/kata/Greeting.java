package de.pruefbit.kata;

import com.sun.istack.internal.NotNull;

class Greeting {

    private final String template;
    private String name;

    Greeting() {
        template = "%s";
    }

    Greeting to(@NotNull String name) {
        this.name = name;
        return this;
    }

    String build() {
        return String.format(template, name);
    }
}
