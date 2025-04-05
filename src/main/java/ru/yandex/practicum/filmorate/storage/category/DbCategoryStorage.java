package ru.yandex.practicum.filmorate.storage.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EmptyResultSelectException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Category;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Repository
public class DbCategoryStorage implements CategoryStorage {

    JdbcTemplate jdbs;

    private static final String FIND_ALL_CATEGORY = """
            SELECT *
            FROM category
            """;

    private static final String ADD_CATEGORY = """
            INSERT IGNORE INTO category (category_name)
            VALUES (?)
            """;

    private static final String UPDATE_CATEGORY = """
            UPDATE category
            SET category_name = ?
            WHERE id = ?
            """;

    private static final String GET_BY_ID = """
            SELECT *
            FROM category
            WHERE id = ?
            """;

    public DbCategoryStorage(JdbcTemplate jdbs) {
        this.jdbs = jdbs;
    }

    @Override
    public Collection<Category> findAll() {
        Collection<Category> category = jdbs.query(FIND_ALL_CATEGORY, getCategoryMapper()).stream()
                .sorted(Comparator.comparingLong(category1 -> category1.getId())).toList();
        log.info("Возвращаем список всех жанров: {}\n", category);
        return category;
    }

    @Override
    public Category add(Category entity) {
        log.info("Создание жанра entity: {} началось", entity);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbs.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(ADD_CATEGORY, new String[]{"id"});
            stmt.setString(1, entity.getName());
            return stmt;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        if (id <= 0) {
            log.error("id для жанра {} не найден", entity);
            throw new IdNotFoundException("id для жанра " + entity + " не найден");
        }
        entity.setId(id);
        return entity;
    }

    @Override
    public Category update(Category entity) {
        log.info("Начинаем обновление жанра {}", entity);
        jdbs.update(UPDATE_CATEGORY, entity.getName(), entity.getId());
        return entity;
    }

    @Override
    public Category getById(long id) {
        log.info("Начинаем получение жанра с id = {}", id);
        List<Category> category = jdbs.query(GET_BY_ID, getCategoryMapper(), id);
        if (!category.isEmpty()) {
            log.info("Получение жанра завершено: {}", category);
            return category.getFirst();
        }
        log.error("Жанр с id = {} не найден", id);
        throw new EmptyResultSelectException("Жанр с id = " + id + " не найден", 0);
    }

    private static RowMapper<Category> getCategoryMapper() {
        return (rs, rowNum) -> Category.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("category_name"))
                .build();
    }
}
