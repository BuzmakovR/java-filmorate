package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
public class Genre {

	private Long id;

	@NotBlank(message = "Наименование жанра должно быть заполнено")
	private String name;
}
