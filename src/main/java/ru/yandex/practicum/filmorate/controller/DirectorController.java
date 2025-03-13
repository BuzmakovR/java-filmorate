package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> getAll() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getByID(@PathVariable long id) {
        return directorService.getDirector(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@Valid @RequestBody Director director) {
        return directorService.addDirector(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director newDirector) {
        return directorService.updateDirector(newDirector);
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@PathVariable long id) {
        directorService.deleteDirector(id);
    }
}
