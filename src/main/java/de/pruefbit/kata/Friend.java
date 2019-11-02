package de.pruefbit.kata;

import java.util.HashMap;
import java.util.Map;

class Friend {

    private final Map<String, String> properties = new HashMap<>();

    private Friend() {}

    static Friend fromMap(Map<String, String> record) {
        Friend friend = new Friend();
        record.forEach(friend.properties::put);
        return friend;
    }

    String get(String property) {
        if (properties.containsKey(property)) {
            return properties.get(property);
        }
        else {
            throw new RuntimeException("can not find property '" + property + "' in data set");
        }
    }
}
