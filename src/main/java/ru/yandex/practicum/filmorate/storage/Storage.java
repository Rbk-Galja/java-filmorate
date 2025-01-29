package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface Storage<T> {
    Collection<T> findAll();

    T add(T entity);

    T update(T entity);

}
