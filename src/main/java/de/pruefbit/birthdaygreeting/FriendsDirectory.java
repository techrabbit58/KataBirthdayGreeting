package de.pruefbit.birthdaygreeting;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

interface FriendsDirectory {

    List<Friend> selectByDate(LocalDate date) throws IOException;
}
