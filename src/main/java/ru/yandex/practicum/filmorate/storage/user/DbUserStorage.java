package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EmptyResultSelectException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
@Primary
@Repository
public class DbUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ALL = """
            SELECT *
            FROM users
            """;

    private static final String GET_BY_ID = """
            SELECT us.id, us.email, us.login, us.name, us.birthday, us.id
            FROM users AS us
            WHERE id = ?
            """;

    private static final String GET_FRIENDS_LIST = """
            SELECT *
            FROM users AS us
            INNER JOIN friends AS fr
            ON us.id = fr.friend_id
            WHERE user_id = ?
            AND status_id != 1
            """;

    private static final String GET_HASHSET_FRIENDS = """
            SELECT fr.friend_id, st.status_name
            FROM friends AS fr
            INNER JOIN status AS st
            ON fr.status_id = st.id
            WHERE fr.status_id != 1
            AND fr.user_id =
            """;

    private static final String ADD_USER = """
            INSERT INTO users (email, login, name, birthday)
            VALUES (?, ?, ?, ?)
            """;

    private static final String ADD_STATUS = """
            INSERT INTO friends (user_id, friend_id, status_id)
            VALUES (?, ?, (SELECT id FROM status WHERE status_name = ?))
            """;

    private static final String UPDATE_USER = """
            UPDATE users
            SET email = ?, login = ?, name = ?, birthday = ?
            WHERE id =
            """;

    private static final String UPDATE_STATUS = """
            UPDATE friends
            SET user_id = ?, friend_id = ?, status_id = (SELECT id FROM status WHERE status_name = ?)
            WHERE user_id =
            """;

    private static final String DELETE_FRIEND = """
            DELETE FROM friends
            WHERE user_id = ? AND friend_id = ?
            """;

    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        Collection<User> result = jdbcTemplate.query(GET_ALL, getUserMapper());
        log.debug("Возвращаем список всех пользоателей:\n {}", result);
        return result;
    }

    @Override
    public User getById(long id) {
        log.info("Получение пользователя с id = {} началось", id);
        List<User> user = jdbcTemplate.query(GET_BY_ID, getUserMapper(), id);
        if (!user.isEmpty()) {
            log.info("Получение пользователя id = {} завершено", id);
            return user.getFirst();
        }
        log.error("User с id = {} не найден", id);
        throw new EmptyResultSelectException("User с id = " + id + " не найден", 0);
    }

    @Override
    public User add(User entity) {
        log.info("Создание пользователя User: {} началось", entity);
        entity.setName(entity.userName(entity));
        KeyHolder key = new GeneratedKeyHolder();
        Date sqlDate = Date.valueOf(entity.getBirthday());
        jdbcTemplate.update(connection -> {
            PreparedStatement stms = connection.prepareStatement(ADD_USER, new String[]{"id"});
            stms.setString(1, entity.getEmail());
            stms.setString(2, entity.getLogin());
            stms.setString(3, entity.getName());
            stms.setDate(4, sqlDate);
            return stms;
        }, key);
        long id = key.getKey().longValue();
        log.info("Пользователю присвоен id = {}", id);
        entity.setId(id);
        log.info("Создание пользователя User: {} завершено", entity);
        return entity;
    }

    @Override
    public User update(User entity) {
        long id = entity.getId();
        User oldUser = getById(id);
        log.info("Обновление пользователя User: {} началось", oldUser);
        entity.setName(entity.userName(entity));
        Date sqlDate = Date.valueOf(entity.getBirthday());
        jdbcTemplate.update(connection -> {
            PreparedStatement stms = connection.prepareStatement(UPDATE_USER + id);
            stms.setString(1, entity.getEmail());
            stms.setString(2, entity.getLogin());
            stms.setString(3, entity.getName());
            stms.setDate(4, sqlDate);
            return stms;
        });
        log.info("Обновление пользователя User: {} завершено", entity);
        return entity;
    }

    @Override
    public List<User> getFriendsList(long id) {
        User user = getById(id);
        if (user == null) {
            log.error("Запрос на получение списка друзей для несуществующего пользователя id = {}", id);
            throw new IdNotFoundException("Запрос от несуществующего пользователя id = " + id);
        }
        log.info("Получаем список друзей для пользователя id = {}", id);
        List<User> friensList = jdbcTemplate.query(GET_FRIENDS_LIST, getUserMapper(), id);
        log.info("Возвращаем список друзей пользователя c id = {}: \n {}", id, friensList);
        return friensList;
    }

    @Override
    public boolean deleteFriendFromTable(long id, long friendId) {
        return jdbcTemplate.update(DELETE_FRIEND, id, friendId) > 0;
    }

    @Override
    public void updateStatus(long userId, long friendId, String status, long sqlId) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(UPDATE_STATUS + sqlId);
            ps.setLong(1, userId);
            ps.setLong(2, friendId);
            ps.setString(3, status);
            return ps;
        });
    }

    @Override
    public void addFriendStatus(long id, long friendId, String status) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ADD_STATUS);
            ps.setLong(1, id);
            ps.setLong(2, friendId);
            ps.setString(3, status);
            return ps;
        });
    }

    @Override
    public LinkedHashMap<Long, FriendshipStatus> getHashSetFriends(long id) {
        LinkedHashMap<Long, FriendshipStatus> friendsList = new LinkedHashMap<>();
        log.info("Начинаем получение списка друзей для пользователя id = {}", id);
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:file:./db/filmorate", "sa", "password");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(GET_HASHSET_FRIENDS + id);
            while (rs.next()) {
                friendsList.put(rs.getLong("friend_id"),
                        (FriendshipStatus.getStatus(rs.getString("status_name"))));
            }

        } catch (SQLException e) {
            log.error("Ошибка получения списка друзей для пользователя id = {}", id);
            System.out.println("Ошибка: " + e.getMessage());
        }
        return friendsList;
    }

    private RowMapper<User> getUserMapper() {
        return (resultset, rowNum) -> User.builder()
                .id(resultset.getLong("id"))
                .email(resultset.getString("email"))
                .name(resultset.getString("name"))
                .login(resultset.getString("login"))
                .birthday(resultset.getDate("birthday").toLocalDate())
                .friends(getHashSetFriends(resultset.getLong("id")))
                .build();
    }
}
