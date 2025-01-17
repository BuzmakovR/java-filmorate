package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.time.Month;

/**
 * Film.
 */
@Data
@Builder
public class Film {

	Long id;

	@NotBlank(message = "Наименование должно быть заполнено")
	String name;

	@Size(max = 200, message = "Максимальная длина описания — 200 символов")
	String description;

	LocalDate releaseDate;

	@Positive(message = "Продолжительность фильма должна быть положительным числом")
	Integer duration;

	public void validate() {
		if (getReleaseDate() != null && getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
			throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
		}
	}

}
