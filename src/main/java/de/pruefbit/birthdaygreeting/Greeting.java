package de.pruefbit.birthdaygreeting;

import com.sun.istack.internal.NotNull;

import java.text.MessageFormat;

class Greeting {
    private final String template;
    private String name;

    Greeting(String template) {
        this.template = template;
    }

    Greeting to(@NotNull String name) {
        this.name = name;
        return this;
    }

    String build() {
        return MessageFormat.format(template, name);
    }
}
