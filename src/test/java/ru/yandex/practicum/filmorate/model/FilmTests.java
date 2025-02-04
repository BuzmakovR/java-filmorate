package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmTests {

	@Test
	void initFilm() {
		Film film = Film.builder().build();
		assertNotNull(film.getUserLikes(), "Поле userLikes не инициализируется при инициализации film");
	}

	@Test
	void filmReleaseDateValidate() {
		Film film = Film.builder()
				.releaseDate(LocalDate.of(1895, Month.DECEMBER, 28).minusDays(1))
				.build();
		assertThrows(ValidationException.class, film::validate, "Валидация даты релиза раньше 28 декабря 1895 года должна вернуть ошибку");

		film = Film.builder()
				.releaseDate(LocalDate.of(1895, Month.DECEMBER, 28))
				.build();
		try {
			film.validate();
		} catch (RuntimeException e) {
			Assertions.fail("Валидация даты релиза 28 декабря 1895 не должна возвращать ошибку");
		}

		film = Film.builder()
				.releaseDate(LocalDate.of(1895, Month.DECEMBER, 28).plusDays(1))
				.build();
		try {
			film.validate();
		} catch (RuntimeException e) {
			Assertions.fail("Валидация даты релиза после 28 декабря 1895 не должна возвращать ошибку");
		}
	}

	@Test
	void filmNameValidate() {
		Film film = Film.builder().build();

		try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = validatorFactory.getValidator();

			Set<ConstraintViolation<Film>> violations = validator.validate(film);
			Assertions.assertNotEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("name"))
					.count(), "Не пройдена валидация имени на пустое значение");

			film = Film.builder()
					.name("")
					.build();

			violations = validator.validate(film);
			Assertions.assertNotEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("name"))
					.count(), "Не пройдена валидация имени на пустое значение");

			film = Film.builder()
					.name("name")
					.build();

			violations = validator.validate(film);
			Assertions.assertEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("name"))
					.count(), "Валидация заполненного имени не должна возвращать ошибку");
		}
	}

	@Test
	void filmDescriptionValidate() {
		Film film = Film.builder().build();

		try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = validatorFactory.getValidator();

			Set<ConstraintViolation<Film>> violations = validator.validate(film);
			Assertions.assertEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("description"))
					.count(), "Валидация описания должна допускать пустое значение");

			film = Film.builder()
					.description("")
					.build();

			violations = validator.validate(film);
			Assertions.assertEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("description"))
					.count(), "Валидация описания должна допускать пустое значение");

			film = Film.builder()
					.description("d".repeat(199))
					.build();

			violations = validator.validate(film);
			Assertions.assertEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("description"))
					.count(), "Валидация описания должна допускать длину до 200 символов");

			film = Film.builder()
					.description("d".repeat(200))
					.build();

			violations = validator.validate(film);
			Assertions.assertEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("description"))
					.count(), "Валидация описания должна допускать длину 200 символов");

			film = Film.builder()
					.description("d".repeat(201))
					.build();

			violations = validator.validate(film);
			Assertions.assertNotEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("description"))
					.count(), "Валидация описания не должна допускать длину более 200 символов");
		}
	}

	@Test
	void filmDurationValidate() {
		Film film = Film.builder().build();

		try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = validatorFactory.getValidator();

			Set<ConstraintViolation<Film>> violations = validator.validate(film);
			Assertions.assertEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("duration"))
					.count(), "Валидация продолжительности должна допускать пустое значение");

			film = Film.builder()
					.duration(1)
					.build();
			violations = validator.validate(film);
			Assertions.assertEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("duration"))
					.count(), "Валидация продолжительности должна допускать положительные значения");

			film = Film.builder()
					.duration(-1)
					.build();
			violations = validator.validate(film);
			Assertions.assertNotEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("duration"))
					.count(), "Валидация продолжительности не должна допускать отрицательные значение");

			film = Film.builder()
					.duration(0)
					.build();
			violations = validator.validate(film);
			Assertions.assertNotEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("duration"))
					.count(), "Валидация продолжительности не должна допускать 0 значение");

		}
	}
}