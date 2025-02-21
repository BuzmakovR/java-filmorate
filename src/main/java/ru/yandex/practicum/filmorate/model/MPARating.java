package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MPARating {

	private Long id;

	@NotBlank(message = "Наименование рейтинга должно быть заполнено")
	private String name;
}
