package de.pruefbit.birthdaygreeting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GreetingTest {

    @Test
    void name_and_template_can_be_set() {
        String name = "Marty";
        String template = "=== {0} ===";
        String expected = "=== " + name + " ===";
        String actual = new Greeting(template).toName(name).build();
        assertEquals(expected, actual);
    }

    @Test
    void the_name_of_the_person_to_greet_can_be_set() {
        String expected = "Marty";
        String actual = new Greeting("{0}").toName(expected).build();
        assertEquals(expected, actual);
    }

    @Test
    void can_not_be_build_without_name_to_greet() {
        assertThrows(RuntimeException.class, () -> new Greeting("{0}").build());
    }

    @Test
    void can_not_be_build_with_zero_length_name() {
        assertThrows(RuntimeException.class, () -> new Greeting("{0}").toName("").build());
    }
}