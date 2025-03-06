package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.feedResource.EventOperation;
import ru.yandex.practicum.filmorate.model.feedResource.EventType;

@Data
public class Feed {
	private Long eventId;

	@NotBlank(message = "Значение не может быть пустым")
	private Long userId;
	private Long entityId;
	private Long timestamp;
	private EventOperation eventOperation;
	private EventType eventType;
}
