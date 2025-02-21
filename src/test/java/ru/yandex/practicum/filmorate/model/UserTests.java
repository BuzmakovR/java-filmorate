package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTests {

	@Test
	void userLoginLogin() {
		User user = User.builder().build();
		assertThrows(ValidationException.class, user::validate, "Валидация пользователя с пустым логином должна вернуть ошибку");

		user = User.builder().login("").build();
		assertThrows(ValidationException.class, user::validate, "Валидация пользователя с пустым логином должна вернуть ошибку");

		user = User.builder().login(" ").build();
		assertThrows(ValidationException.class, user::validate, "Валидация пользователя с пробелом в логине должна вернуть ошибку");

		user = User.builder().login("1 ").build();
		assertThrows(ValidationException.class, user::validate, "Валидация пользователя с пробелом в логине должна вернуть ошибку");

		try {
			user = User.builder().login("login2").build();
			user.validate();
		} catch (RuntimeException e) {
			Assertions.fail("Валидация корректного значения логина не должна возвращать ошибку", e);
		}
	}

	@Test
	void userEmailValidate() {
		User user = User.builder()
				.build();

		try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = validatorFactory.getValidator();

			Set<ConstraintViolation<User>> violations = validator.validate(user);
			Assertions.assertNotEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("email"))
					.count(), "Не пройдена валидация email на пустое значение");

			user = User.builder()
					.email("")
					.build();

			violations = validator.validate(user);
			Assertions.assertNotEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("email"))
					.count(), "Не пройдена валидация email на пустое значение");

			user = User.builder()
					.email("email")
					.build();

			violations = validator.validate(user);
			Assertions.assertNotEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("email"))
					.count(), "Не пройдена валидация email на формат");

			user = User.builder()
					.email("email@email.ru")
					.build();

			violations = validator.validate(user);
			Assertions.assertEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("email"))
					.count(), "Валидация корректного email не должна возвращать ошибку");
		}
	}

	@Test
	void userBirthdayValidate() {
		User user = User.builder()
				.login("user-test")
				.birthday(LocalDate.now().plusDays(1))
				.build();

		try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = validatorFactory.getValidator();

			Set<ConstraintViolation<User>> violations = validator.validate(user);
			Assertions.assertNotEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("birthday"))
					.count(), "Не пройдена валидация birthday на значение в будущем");

			user = User.builder()
					.login("user-test")
					.birthday(LocalDate.now())
					.build();

			violations = validator.validate(user);
			Assertions.assertEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("birthday"))
					.count(), "Не пройдена валидация birthday на значение текущей даты");

			user = User.builder()
					.login("user-test")
					.birthday(LocalDate.now().minusDays(1))
					.build();

			violations = validator.validate(user);
			Assertions.assertEquals(0, violations.stream()
					.filter(v -> v.getPropertyPath().toString().equals("birthday"))
					.count(), "Не пройдена валидация birthday на значение прошедшей даты");

		}
	}
}
