package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaRatingController {

	@Autowired
	private final MpaRatingService mpaRatingService;

	@GetMapping
	public Collection<MpaRating> findAll() {
		log.info("Запрос на получение жанров");

		Collection<MpaRating> rating = mpaRatingService.getRatings();

		log.debug("Список рейтингов: {}", rating);

		return rating;
	}

	@GetMapping("/{id}")
	public MpaRating get(@PathVariable("id") long id) {
		log.info("Запрос на получение рейтинга с ID: {}", id);

		MpaRating rating = mpaRatingService.getRating(id);
		log.debug("Полученный рейтинг: {}", rating);

		return rating;
	}
}
