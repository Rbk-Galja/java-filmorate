package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Category;
import ru.yandex.practicum.filmorate.storage.category.CategoryStorage;

import java.util.Collection;

@Service
public class CategoryService {

    private final CategoryStorage categoryStorage;

    @Autowired
    public CategoryService(CategoryStorage categoryStorage) {
        this.categoryStorage = categoryStorage;
    }

    public Collection<Category> findAll() {
        return categoryStorage.findAll();
    }

    public Category add(Category entity) {
        return categoryStorage.add(entity);
    }

    public Category update(Category entity) {
        return categoryStorage.update(entity);
    }

    public Category getById(long id) {
        return categoryStorage.getById(id);
    }
}
