package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.RatingMPAA;
import ru.yandex.practicum.filmorate.storage.MPAA.DbRateMPAAStorage;

import java.util.Collection;

@Service
public class RatingMPAAService {

    private final DbRateMPAAStorage mpaaStorage;

    @Autowired
    public RatingMPAAService(DbRateMPAAStorage mpaaStorage) {
        this.mpaaStorage = mpaaStorage;
    }

    public Collection<RatingMPAA> findAll() {
        return mpaaStorage.findAll();
    }

    public RatingMPAA getById(long id) {
        return mpaaStorage.getById(id);
    }

    public RatingMPAA add(RatingMPAA entity) {
        return mpaaStorage.add(entity);
    }

    public RatingMPAA update(RatingMPAA entity) {
        return mpaaStorage.update(entity);
    }
}
