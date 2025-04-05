package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EmptyResultSelectException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Category;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMPAA;
import ru.yandex.practicum.filmorate.storage.MPAA.DbRateMPAAStorage;
import ru.yandex.practicum.filmorate.storage.category.DbCategoryStorage;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
@Primary
@Repository
public class DbFilmStorage implements FilmStorage {

    @Autowired
    DbUserStorage userStorage;

    @Autowired
    DbRateMPAAStorage mpaaStorage;

    @Autowired
    DbCategoryStorage categoryStorage;

    JdbcTemplate jdbs;

    private static final String GET_BY_ID = """
            SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, f.id
            FROM film AS f
            WHERE f.id = ?
            """;

    private static final String GET_ALL = """
            SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, f.id
            FROM film AS f
            """;

    private static final String GET_CATEGORY_ID = """
            SELECT category_id
            FROM film_category
            WHERE film_id =
            """;

    private static final String ADD_FILM = """
            INSERT INTO film (name, description, releaseDate, duration)
            VALUES (?, ?, ?, ?)
            """;

    private static final String ADD_RATING = """
            UPDATE film
            SET rating_id = (SELECT id FROM rateMPAA WHERE rate_MPAA = ?)
            WHERE id = ?
            """;

    private static final String ADD_CATEGORY = """
            INSERT IGNORE INTO film_category (category_id, film_id)
            VALUES (SELECT id FROM category WHERE category_name = ?, ?);
            """;

    private static final String ADD_LIKE = """
            INSERT IGNORE INTO likes (film_id, user_id)
            VALUES (?, ?)
            """;

    private static final String UPDATE_FILM = """
            UPDATE film
            SET name = ?, description = ?, releaseDate = ?, duration = ?
            WHERE id = ?
            """;

    private static final String DELETE_CATEGORY = """
            DELETE FROM film_category
            WHERE film_id = ?
            """;

    private static final String DELETE_LIKE = """
            DELETE FROM likes
            WHERE film_id = ? AND user_id = ?
            """;

    private static final String RATE_PLUS = """
            INSERT INTO film_rate (film_id, rate)
            VALUES (?, 1)
            ON DUPLICATE KEY
            UPDATE rate = rate + 1
            """;

    private static final String RATE_MINUS = """
            UPDATE film_rate
            SET rate = rate - 1
            WHERE film_id =
            """;

    private static final String COUNT_POPULAR_FILM = """
            SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, f.id, fr.rate
            FROM film AS f
            INNER JOIN film_rate AS fr
            ON f.id = fr.film_id
            GROUP BY f.id
            ORDER BY (fr.rate) DESC
            LIMIT ?
            """;

    public DbFilmStorage(JdbcTemplate jdbs) {
        this.jdbs = jdbs;
    }

    @Override
    public Collection<Film> findAll() {
        Collection<Film> film = jdbs.query(GET_ALL, getFilmMapper());
        log.debug("Возвращаем список всех фильмов {}:\n", film);
        return film;
    }

    @Override
    public Film getById(long id) {
        log.info("Получение фильма с id = {} началось", id);
        List<Film> film = jdbs.query(GET_BY_ID, getFilmMapper(), id);
        if (!film.isEmpty()) {
            log.info("Получение фильма {} завершено", film);
            return film.getFirst();
        }
        log.error("Фильм с id = {} не найден", id);
        throw new EmptyResultSelectException("Фильм с id = " + id + " не найден", 0);
    }

    @Override
    public Film add(Film entity) {
        log.info("Создание фильма Film: {} началось", entity);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Date sqlDate = Date.valueOf(entity.getReleaseDate());
        jdbs.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(ADD_FILM, new String[]{"id"});
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getDescription());
            stmt.setDate(3, sqlDate);
            stmt.setInt(4, entity.getDuration());
            return stmt;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        if (id <= 0) {
            log.error("id для фильма {} не найден", entity);
            throw new IdNotFoundException("id для фильма " + entity + " не найден");
        }
        log.info("Фильму присвоен id = {}", id);
        entity.setId(id);
        entity.setMpa(ratingMapper(entity));
        entity.setGenres(categoryMapper(entity));
        addCategoryOnFilm(entity);
        addRatingFilm(entity.getMpa(), id);
        log.info("Создание фильма Film: {} завершено", entity);
        return entity;
    }

    @Override
    public Film update(Film entity) {
        long id = entity.getId();
        Film oldFilm = getById(id);
        log.info("Обновление фильма Film: {} началось", oldFilm);
        Date sqlDate = Date.valueOf(entity.getReleaseDate());
        jdbs.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(UPDATE_FILM);
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getDescription());
            stmt.setDate(3, sqlDate);
            stmt.setInt(4, entity.getDuration());
            stmt.setLong(5, id);
            return stmt;
        });
        entity.setMpa(ratingMapper(entity));
        deleteCategory(id);
        addCategoryOnFilm(entity);
        log.info("Обновление фильма Film завершено: {}", entity);
        return entity;
    }

    @Override
    public Film likeFilm(long id, long userId) {
        Film film = getById(id);
        if (film == null) {
            log.error("Запрос добавления лайка для несуществующего id = {}", id);
            throw new IdNotFoundException("Фильм с указанным id = " + id + " не найден");
        }
        if (userStorage.getById(userId) == null) {
            log.error("Запрос добавления лайка от несуществующего пользователя id = {}", userId);
            throw new IdNotFoundException("Пользователь с указанным id = " + userId + " не найден");
        }
        int i = jdbs.update(ADD_LIKE, id, userId);
        log.info("Лайк для фильма {} добавлен", film);
        if (i > 0) {
            ratePlus(id);
        }
        return film;
    }

    @Override
    public Film deleteLike(long id, long userId) {
        Film film = getById(id);
        if (film == null) {
            log.error("Запрос на удаление лайка для несуществующего id = {}", id);
            throw new IdNotFoundException("Фильм с указанным id = " + id + " не найден");
        }
        if (userStorage.getById(userId) == null) {
            log.error("Запрос на удаление лайка от несуществующего пользователя id = {}", userId);
            throw new IdNotFoundException("Пользователь с указанным id = " + userId + " не найден");
        }
        jdbs.update(DELETE_LIKE, id, userId);
        log.info("Лайк у фильма {} удален", film);
        rateMinus(id);
        return film;
    }

    @Override
    public List<Film> countPopularFilm(int count) {
        List<Film> countFilm = jdbs.query(COUNT_POPULAR_FILM, getFilmMapper(), count);
        log.info("Возвращаем запрошенный список фильмов с максимальным количеством лайков {}", countFilm);
        return countFilm;
    }

    private RatingMPAA ratingMapper(Film entity) {
        if (entity.getMpa() == null) {
            log.error("Рейтинг для фильма с {} не найден", entity);
            return null;
        }
        long id = entity.getMpa().getId();
        return mpaaStorage.getById(id);
    }

    private void addRatingFilm(RatingMPAA mpa, long id) {
        if (mpa == null) {
            return;
        }
        jdbs.update(con -> {
            PreparedStatement stmt = con.prepareStatement(ADD_RATING);
            stmt.setString(1, mpa.getName());
            stmt.setLong(2, id);
            return stmt;
        });
    }

    private void rateMinus(long id) {
        jdbs.update(RATE_MINUS + id);
    }

    private void ratePlus(long id) {
        jdbs.update(RATE_PLUS, id);
    }

    private void addCategoryOnFilm(Film entity) {
        List<Category> category = categoryMapper(entity).stream().toList();
        log.info("Добавление жанров {} фильму id = {} началось", category, entity.getId());
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection("jdbc:h2:file:./db/filmorate", "sa", "password");
            PreparedStatement ps = con.prepareStatement(ADD_CATEGORY);
            con.setAutoCommit(false);
            for (int i = 0; i < category.size(); i++) {
                ps.setString(1, category.get(i).getName());
                ps.setLong(2, entity.getId());
                ps.addBatch();
            }
            ps.executeBatch();
            log.info("Добавление жанров к фильму id = {} завершено", entity.getId());
            con.commit();
        } catch (Exception e) {
            log.error("Ошибка добавления жанров к фильму id = {}", entity.getId());
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private LinkedHashSet<Category> getCategoryFilm(long id) {
        LinkedHashSet<Category> category = new LinkedHashSet<>();
        log.info("Получение id жанров для фильма id = {} началось", id);
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:file:./db/filmorate", "sa", "password");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(GET_CATEGORY_ID + id);
            while (rs.next()) {
                category.add(categoryStorage.getById(rs.getLong(1)));
            }

        } catch (SQLException e) {
            log.error("Ошибка получения жанров для фильма id = {}", id);
            System.out.println("Ошибка: " + e.getMessage());
        }
        log.info("Получение жанров:\n {} для фильма id = {} завершено", category, id);
        return category;
    }

    private boolean deleteCategory(long id) {
        log.info("Удаление жанров для фильма id = {} началось", id);
        boolean result = jdbs.update(DELETE_CATEGORY, id) > 0;
        if (result) {
            log.info("Удаление жанров для фильма id = {} завершено", id);
        } else {
            log.info("Жанры для фильма id = {} не найдены", id);
        }
        return result;
    }

    private RowMapper<Film> getFilmMapper() {
        return (resultset, rowNum) -> Film.builder()
                .id(resultset.getLong("id"))
                .name(resultset.getString("name"))
                .description(resultset.getString("description"))
                .releaseDate(resultset.getDate("releaseDate").toLocalDate())
                .duration(resultset.getInt("duration"))
                .mpa(getFilmRatingMapper(resultset.getLong("rating_id")))
                .genres(getCategoryFilm(resultset.getLong("id")))
                .build();
    }

    private LinkedHashSet<Category> categoryMapper(Film entity) {
        log.info("Добавление жанров {} к объекту Film началось", entity.getGenres());
        LinkedHashSet<Category> result = new LinkedHashSet<>();
        if (entity.getGenres() == null) {
            return result;
        }
        List<Category> category = entity.getGenres().stream().toList();
        log.info("Получаем список всех жанров сохраненных в объекте фильм {}", category);
        for (int i = 0; i < category.size(); i++) {
            long id = category.get(i).getId();
            result.add(categoryStorage.getById(id));
        }
        log.info("Добавление жанров завершено {}", result);
        return result;
    }

    private RatingMPAA getFilmRatingMapper(long id) {
        if (id == 0) {
            return null;
        }
        return mpaaStorage.getById(id);
    }
}
