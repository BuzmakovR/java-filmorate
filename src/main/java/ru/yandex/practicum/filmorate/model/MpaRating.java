package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class MpaRating {

    private Long id;

    @NotBlank(message = "Наименование рейтинга должно быть заполнено")
    private String name;
}
