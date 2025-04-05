package ru.yandex.practicum.filmorate.storage.MPAA;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EmptyResultSelectException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMPAA;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Primary
@Repository
public class DbRateMPAAStorage implements RateMPAAStorage {

    JdbcTemplate jdbs;

    public DbRateMPAAStorage(JdbcTemplate jdbs) {
        this.jdbs = jdbs;
    }

    private static final String GET_ALL = """
            SELECT *
            FROM rateMPAA
            """;

    private static final String GET_BY_ID = """
            SELECT *
            FROM rateMPAA
            WHERE id = ?
            """;

    private static final String ADD_MPAA = """
            INSERT INTO rateMPAA (rate_MPAA)
            VALUES (?)
            """;

    private static final String UPDATE_MPAA = """
            UPDATE rateMPAA
            SET rate_MPAA = ?
            WHERE id = ?
            """;

    @Override
    public Collection<RatingMPAA> findAll() {
        Collection<RatingMPAA> rating = jdbs.query(GET_ALL, getRatingMPAAMapper()).stream()
                .sorted(Comparator.comparingLong(ratingMPAA -> ratingMPAA.getId())).toList();
        log.info("Возвращаем список всех категорий {}", rating);
        return rating;
    }

    @Override
    public RatingMPAA getById(long id) {
        log.info("Получение рейтинга MPAA с id = {}", id);
        List<RatingMPAA> mpaa = jdbs.query(GET_BY_ID, getRatingMPAAMapper(), id);
        if (!mpaa.isEmpty()) {
            log.info("Получение рейтинга MPAA с id = {} завершено", id);
            return mpaa.getFirst();
        }
        log.error("Рейтинг с id = {} не найден", id);
        throw new EmptyResultSelectException("Рейтинг с id = " + id + " не найден", 0);
    }

    @Override
    public RatingMPAA add(RatingMPAA entity) {
        log.info("Создание рейтинга: {} началось", entity);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbs.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(ADD_MPAA, new String[]{"id"});
            stmt.setString(1, entity.getName());
            return stmt;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        if (id <= 0) {
            log.error("id для рейтинга {} не найден", entity);
            throw new IdNotFoundException("id для рейтинга " + entity + " не найден");
        }
        log.info("Рейтингу присвоен id = {}", id);
        entity.setId(id);
        log.info("Создание рейтинга: {} завершено", entity);
        return entity;
    }

    @Override
    public RatingMPAA update(RatingMPAA entity) {
        log.info("Начинаем обновление рейтинга {}", entity);
        jdbs.update(UPDATE_MPAA, entity.getName(), entity.getId());
        return entity;
    }

    private static RowMapper<RatingMPAA> getRatingMPAAMapper() {
        return (resultset, rowNum) -> RatingMPAA.builder()
                .id(resultset.getLong("id"))
                .name(resultset.getString("rate_MPAA"))
                .build();
    }
}
