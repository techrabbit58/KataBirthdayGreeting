package de.pruefbit.kata;

import java.util.HashMap;
import java.util.Map;

class Friend {

    private final Map<String, String> attributes = new HashMap<>();

    private Friend() {}

    static Friend fromMap(Map<String, String> record) {
        Friend friend = new Friend();
        record.forEach(friend.attributes::put);
        return friend;
    }

    String get(String attribute) {
        if (attributes.containsKey(attribute)) {
            return attributes.get(attribute);
        }
        else {
            throw new RuntimeException("can not find property '" + attribute + "' in data set");
        }
    }
}
