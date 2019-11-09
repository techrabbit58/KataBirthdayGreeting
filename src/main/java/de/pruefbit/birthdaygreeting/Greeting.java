package de.pruefbit.birthdaygreeting;

import java.text.MessageFormat;

class Greeting {
    private final String template;
    private String name = null;

    Greeting(String template) {
        this.template = template;
    }

    Greeting toName(String name) {
        this.name = name;
        return this;
    }

    String build() {
        if (name == null || name.equals("")) {
            throw new RuntimeException("the name of the notified person may not be null nor empty");
        }
        return MessageFormat.format(template, name);
    }
}
