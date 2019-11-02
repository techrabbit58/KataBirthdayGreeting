package de.pruefbit.kata;

public enum PropertyNames {
    GREETING_TEMPLATE("greetingTemplate"),
    ADDRESS("address"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName");

    final String text;

    PropertyNames(String text) {
        this.text = text;
    }
}
