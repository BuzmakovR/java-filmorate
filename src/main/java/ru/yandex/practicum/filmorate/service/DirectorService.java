package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

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
        return directorStorage.getById(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссер с ID " + directorId + " не найден"));
    }

    public List<Director> getAllDirectors() {
        log.info("Запрос на получение всех режиссеров");
        List<Director> directors = directorStorage.getAll();
        log.debug("Найдено {} режиссеров", directors.size());
        return directors;
    }

    public Director addDirector(Director director) {
        log.info("Добавление нового режиссера: {}", director);
        director.setId(generateId());
        Director addedDirector = directorStorage.create(director);
        log.info("Режиссер успешно добавлен с ID {}", addedDirector.getId());
        return addedDirector;
    }

    public Director updateDirector(Director newDirector) {
        log.info("Обновление режиссера с ID {}", newDirector.getId());
        if (directorStorage.getById(newDirector.getId()).isEmpty()) {
            log.error("При обновлении режиссер с ID {} не найден", newDirector.getId());
            throw new NotFoundException("Режиссер не найден");
        }
        Director updatedDirector = directorStorage.update(newDirector);
        log.info("Режиссер с ID {} успешно обновлен", updatedDirector.getId());
        return updatedDirector;
    }

    public void deleteDirector(Long directorId) {
        log.info("Удаление режиссера с ID {}", directorId);
        if (directorStorage.getById(directorId).isEmpty()) {
            log.error("При удалении режиссер с ID {} не найден", directorId);
            throw new NotFoundException("Режиссер не найден");
        }
        directorStorage.deleteById(directorId);
        log.info("Режиссер с ID {} успешно удален", directorId);
    }

    private Long generateId() {
        Long currentId = directorStorage.getAll().stream()
                .mapToLong(Director::getId)
                .max()
                .orElse(0);
        return ++currentId;
    }
}
