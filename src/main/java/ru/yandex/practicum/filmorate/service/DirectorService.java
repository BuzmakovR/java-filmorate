package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;

@Slf4j
@Service
public class DirectorService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(@Qualifier("directorDbStorage") DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director getDirector(Long directorId) {
        log.info("Запрос на получение режиссера с ID {}", directorId);
        return directorStorage.getById(directorId);
    }

    public Collection<Director> getAllDirectors() {
        log.info("Запрос на получение всех режиссеров");
        Collection<Director> directors = directorStorage.getAll();
        log.debug("Найдено {} режиссеров", directors.size());
        return directors;
    }

    public Director addDirector(Director director) {
        log.info("Добавление нового режиссера: {}", director);
        // Значение id будет сгенерировано БД
        Director addedDirector = directorStorage.create(director);
        log.info("Режиссер успешно добавлен с ID {}", addedDirector.getId());
        return addedDirector;
    }

    public Director updateDirector(Director newDirector) {
        log.info("Обновление режиссера с ID {}", newDirector.getId());
        Director updatedDirector = directorStorage.update(newDirector);
        log.info("Режиссер с ID {} успешно обновлен", updatedDirector.getId());
        return updatedDirector;
    }

    public void deleteDirector(Long directorId) {
        log.info("Удаление режиссера с ID {}", directorId);
        directorStorage.deleteById(directorId);
        log.info("Режиссер с ID {} успешно удален", directorId);
    }
}
