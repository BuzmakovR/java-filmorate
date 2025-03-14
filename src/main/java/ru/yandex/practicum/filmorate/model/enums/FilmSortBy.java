package ru.yandex.practicum.filmorate.model.enums;

public enum FilmSortBy {
	YEAR,
	LIKES;

	public static FilmSortBy fromString(String sortBy) {
		for (FilmSortBy sort : FilmSortBy.values()) {
			if (sort.name().equalsIgnoreCase(sortBy)) {
				return sort;
			}
		}
		throw new IllegalArgumentException("Некорректный параметр сортировки: " + sortBy);
	}
}
