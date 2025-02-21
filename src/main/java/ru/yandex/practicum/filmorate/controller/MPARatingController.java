package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.service.MPARatingService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MPARatingController {

	@Autowired
	private final MPARatingService mpaRatingService;

	@GetMapping
	public Collection<MPARating> findAll() {
		log.info("Запрос на получение жанров");

		Collection<MPARating> rating = mpaRatingService.getRatings();

		log.debug("Список рейтингов: {}", rating);

		return rating;
	}

	@GetMapping("/{id}")
	public MPARating get(@PathVariable("id") long id) {
		log.info("Запрос на получение рейтинга с ID: {}", id);

		MPARating rating = mpaRatingService.getRating(id);
		log.debug("Полученный рейтинг: {}", rating);

		return rating;
	}
}
