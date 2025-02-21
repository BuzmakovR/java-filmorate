package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

@Data
@Builder
public class User {

	private Long id;

	@Email
	@NotBlank(message = "Email пользователя должен быть заполнен")
	private String email;

	@NotBlank(message = "Логин пользователя должен быть заполнен")
	private String login;

	private String name;

	@PastOrPresent(message = "Дата рождения пользователя не может быть в будущем")
	private LocalDate birthday;

	public void validate() {
		if (getLogin() == null || getLogin().isBlank() || getLogin().contains(" ")) {
			throw new ValidationException("Логин пользователя не может быть пустым и содержать пробелы");
		}
	}

}
