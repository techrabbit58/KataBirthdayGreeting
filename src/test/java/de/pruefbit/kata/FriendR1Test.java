package de.pruefbit.kata;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FriendR1Test {
    static private final Map<String, String> theMap = new HashMap<>();
    static private Friend friend;

    static {
        theMap.put("name", "John");
        theMap.put("age", "42");
    }

    @BeforeAll
    static void setUpAll() {
        friend = Friend.fromMap(theMap);
    }

    @Test
    void could_be_built_fromMap() {
        theMap.forEach((key, value) -> assertEquals(theMap.get(key), friend.get(key)));
    }

    @Test
    void throws_RuntimeError_on_blemished_get() {
        assertThrows(RuntimeException.class, () -> friend.get("foo"));
    }
}