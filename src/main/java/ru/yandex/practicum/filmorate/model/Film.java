package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {

	private Long id;

	@NotBlank(message = "Наименование фильма должно быть заполнено")
	private String name;

	@Size(max = 200, message = "Максимальная длина описания фильма — 200 символов")
	private String description;

	private LocalDate releaseDate;

	@Positive(message = "Продолжительность фильма должна быть положительным числом")
	private Integer duration;

	private final Set<Long> userLikes = new HashSet<>();

	public void validate() {
		if (getReleaseDate() != null && getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
			throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
		}
	}

	public Set<Long> getUserLikes() {
		return Set.copyOf(userLikes);
	}

	public void addUserLike(final Long userId) {
		userLikes.add(userId);
	}

	public void deleteUserLike(final Long userId) {
		userLikes.remove(userId);
	}

}
