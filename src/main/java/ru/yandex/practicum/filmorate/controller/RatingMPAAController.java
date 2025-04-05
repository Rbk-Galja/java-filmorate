package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.RatingMPAA;
import ru.yandex.practicum.filmorate.service.RatingMPAAService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
public class RatingMPAAController {

    private final RatingMPAAService ratingMPAAService;

    @GetMapping
    public Collection<RatingMPAA> findAll() {
        return ratingMPAAService.findAll();
    }

    @GetMapping("/{id}")
    public RatingMPAA getById(@PathVariable long id) {
        return ratingMPAAService.getById(id);
    }

    @PostMapping
    public RatingMPAA add(@RequestBody RatingMPAA entity) {
        return ratingMPAAService.add(entity);
    }

    @PutMapping
    public RatingMPAA update(@RequestBody RatingMPAA entity) {
        return ratingMPAAService.update(entity);
    }
}
