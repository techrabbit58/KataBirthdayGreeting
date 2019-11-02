package de.pruefbit.kata;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

interface FriendsDirectory {

    List<Friend> selectByDate(@NotNull LocalDate date) throws IOException;
}
