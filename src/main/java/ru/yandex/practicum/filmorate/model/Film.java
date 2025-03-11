package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Builder
@EqualsAndHashCode(of = {"name", "releaseDate", "mpa"})
public class Film {

	private Long id;

	@NotBlank(message = "Наименование фильма должно быть заполнено")
	private String name;

	@Size(max = 200, message = "Максимальная длина описания фильма — 200 символов")
	private String description;

	private LocalDate releaseDate;

	@Positive(message = "Продолжительность фильма должна быть положительным числом")
	private Integer duration;

	private MpaRating mpa;

	private final Collection<Genre> genres = new ArrayList<>();

	public void validate() {
		if (getReleaseDate() != null && getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
			throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
		}
	}

	public void addGenre(Genre genre) {
		if (!genres.contains(genre)) {
			genres.add(genre);
		}
	}

	public void removeGenre(Genre genre) {
		genres.remove(genre);
	}

	public void clearGenre() {
		genres.clear();
	}

	public void setGenres(Collection<Genre> genres) {
		this.genres.clear();
		if (genres != null && !genres.isEmpty()) {
			this.genres.addAll(genres);
		}
	}



}
