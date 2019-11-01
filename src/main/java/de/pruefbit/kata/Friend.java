package de.pruefbit.kata;

import java.util.Map;

class Friend {

    private String lastName;
    private String firstName;
    private String dateOfBirth;
    private String email;

    private Friend() {}

    static Friend fromMap(Map<String, String> record) {
        Friend friend = new Friend();
        friend.lastName = record.get("last_name");
        friend.firstName = record.get("first_name");
        friend.dateOfBirth = record.get("date_of_birth");
        friend.email = record.get("email");
        return friend;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    String getFirstName() {
        return firstName;
    }

    String getEmail() {
        return email;
    }
}
