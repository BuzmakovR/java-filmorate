package ru.yandex.practicum.filmorate.model;

public enum DirectorSortBy {
    YEAR,
    LIKES;

    public static DirectorSortBy fromString(String sortBy) {
        for (DirectorSortBy sort : DirectorSortBy.values()) {
            if (sort.name().equalsIgnoreCase(sortBy)) {
                return sort;
            }
        }
        throw new IllegalArgumentException("Некорректный параметр сортировки: " + sortBy);
    }
}
