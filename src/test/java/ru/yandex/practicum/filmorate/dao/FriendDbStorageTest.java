package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendDbStorageTest {
    private final FriendDbStorage friendDbStorage;
    private final UserDbStorage userDbStorage;

    @Test
    void addGetDeleteFriendsTest() throws SQLException {
        User user = new User(1, "test@ya.ru","login", "name",
                LocalDate.of(1990,1,1));
        User friend = new User(2, "test2@ya.ru","login2", "name2",
                LocalDate.of(1995,1,1));
        userDbStorage.create(user);
        userDbStorage.create(friend);

        friendDbStorage.addToFriends(user.getId(), friend.getId());
        assertEquals(1, friendDbStorage.getListFriends(user.getId()).size());

        friendDbStorage.removeFromFriends(user.getId(), friend.getId());
        assertEquals(0, friendDbStorage.getListFriends(user.getId()).size());
    }

    @Test
    void getListCommonFriendsTest() {
        User user = new User(1, "test3@ya.ru","login3", "name3",
                LocalDate.of(2000,1,1));
        User friend = new User(2, "test4@ya.ru","login4", "name4",
                LocalDate.of(1999,1,1));
        User userCommon = new User(3, "test5@ya.ru","login5", "name5",
                LocalDate.of(2005,1,1));
        userDbStorage.create(user);
        userDbStorage.create(friend);
        userDbStorage.create(userCommon);

        friendDbStorage.addToFriends(user.getId(), userCommon.getId());
        friendDbStorage.addToFriends(friend.getId(), userCommon.getId());
        assertEquals(1, friendDbStorage.getListMutualFriends(user.getId(), friend.getId()).size());
    }
}