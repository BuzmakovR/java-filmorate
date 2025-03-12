package ru.yandex.practicum.filmorate.storage.impl.repositories.mappers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        log.debug("Начало маппинга строки ResultSet в объект Film. Номер строки: {}", rowNum);

        Function<Integer, Integer> getDuration = d -> d > 0 ? d : null;

        // Создание объекта Film
        Film film = Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(getDuration.apply(rs.getInt("duration")))
                .build();
        log.debug("Создан объект Film: {}", film);

        // Установка рейтинга MPA
        long mpaId = rs.getLong("mpa_rating_id");
        String mpaName = rs.getString("mpa_rating_name");
        if (mpaId > 0 && mpaName != null && !mpaName.isBlank()) {
            film.setMpa(MpaRating.builder().id(mpaId).name(mpaName).build());
            log.debug("Установлен рейтинг MPA для фильма: {}", film.getMpa());
        } else {
            log.debug("Рейтинг MPA не установлен, так как данные отсутствуют или некорректны");
        }

        // Установка даты релиза
        Optional.ofNullable(rs.getDate("release_date"))
                .ifPresent(date -> {
                    film.setReleaseDate(date.toLocalDate());
                    log.debug("Установлена дата релиза для фильма: {}", film.getReleaseDate());
                });

        // Обработка жанров
        List<String> genreIds = Optional.ofNullable(rs.getString("genre_id"))
                .map(s -> List.of(s.split(",")))
                .orElse(List.of());
        log.debug("Найдены ID жанров: {}", genreIds);

        List<String> genreNames = Optional.ofNullable(rs.getString("genre_name"))
                .map(s -> List.of(s.split(",")))
                .orElse(List.of());
        log.debug("Найдены названия жанров: {}", genreNames);

        for (int i = 0; i < genreIds.size(); i++) {
            if (i >= genreNames.size()) break;
            Genre genre = Genre.builder()
                    .id(Long.valueOf(genreIds.get(i)))
                    .name(genreNames.get(i))
                    .build();
            film.addGenre(genre);
            log.debug("Добавлен жанр в фильм: {}", genre);
        }

        // Обработка режиссёров
        List<String> directorIds = Optional.ofNullable(rs.getString("director_id"))
                .map(s -> List.of(s.split(",")))
                .orElse(List.of());
        List<String> directorNames = Optional.ofNullable(rs.getString("director_name"))
                .map(s -> List.of(s.split(",")))
                .orElse(List.of());
        for (int i = 0; i < directorIds.size(); i++) {
            if (i >= directorNames.size()) break;
            Director director = Director.builder()
                    .id(Long.valueOf(directorIds.get(i)))
                    .name(directorNames.get(i))
                    .build();
            film.getDirectors().add(director);
            log.debug("Добавлен режиссёр в фильм: {}", director);
        }

        log.debug("Завершение маппинга строки ResultSet в объект Film. Результат: {}", film);
        return film;
    }
}