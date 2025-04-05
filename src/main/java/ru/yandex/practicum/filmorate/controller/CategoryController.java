package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Category;
import ru.yandex.practicum.filmorate.service.CategoryService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Collection<Category> findAllCategory() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable long id) {
        return categoryService.getById(id);
    }

    @PostMapping
    public Category addCategory(@RequestBody Category category) {
        return categoryService.add(category);
    }

    @PutMapping
    public Category update(@RequestBody Category category) {
        return categoryService.update(category);
    }
}
