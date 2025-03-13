package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import ru.yandex.practicum.filmorate.model.feedResource.EventOperation;
import ru.yandex.practicum.filmorate.model.feedResource.EventType;

@Getter
@Builder
public class Feed {
	private Long eventId;

	@NotBlank(message = "Значение не может быть пустым")
	private Long userId;
	private Long entityId;
	private Long timestamp;

	@JsonProperty("operation")
	private EventOperation eventOperation;
	private EventType eventType;
}
