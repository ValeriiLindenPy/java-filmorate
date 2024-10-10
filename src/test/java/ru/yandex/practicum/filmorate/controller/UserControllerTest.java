package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.ValidationMarker;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
	private Validator validator;
	private UserController controller;

	@BeforeEach
	void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		controller = new UserController();
	}

	@Test
	void userCreate201() throws Exception {
		User user = User.builder()
				.email("test@mail.com")
				.login("testUser")
				.name("Test Name")
				.birthday(LocalDate.of(1990, 5, 15))
				.build();

		User userCreated = controller.create(user);

		assertFalse(controller.users.isEmpty());

		Set<ConstraintViolation<User>> violations = validator.validate(userCreated);

		assertTrue(violations.isEmpty());
	}

	@Test
	void userCreateFailEmailInvalid() throws Exception {
		User user = User.builder()
				.email("invalid-email")
				.login("testUser")
				.name("Test Name")
				.birthday(LocalDate.of(1990, 5, 15))
				.build();

		Set<ConstraintViolation<User>> violations = validator.validate(user);

		assertFalse(violations.isEmpty());
		assertThat(violations).hasSize(1);
		assertThat(violations).extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("Некорректный формат email.");
	}

	@Test
	void userCreateFailEmailBlank() throws Exception {
		User user = User.builder()
				.email("")
				.login("testUser")
				.name("Test Name")
				.birthday(LocalDate.of(1990, 5, 15))
				.build();

		Set<ConstraintViolation<User>> violations = validator.validate(user);

		assertFalse(violations.isEmpty());
		assertThat(violations).hasSize(1);
		assertThat(violations).extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("Email не может быть null.");
	}

	@Test
	void userCreateFailLoginBlank() throws Exception {
		User user = User.builder()
				.email("test@mail.com")
				.login("")
				.name("Test Name")
				.birthday(LocalDate.of(1990, 5, 15))
				.build();

		Set<ConstraintViolation<User>> violations = validator.validate(user);

		assertFalse(violations.isEmpty());
		assertThat(violations).hasSize(2);
		assertThat(violations).extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("Логин не должен содержать пробелы.",
						"Логин не может быть пустым.");
	}

	@Test
	void userCreateFailLoginWithSpaces() throws Exception {
		User user = User.builder()
				.email("test@mail.com")
				.login("test user")
				.name("Test Name")
				.birthday(LocalDate.of(1990, 5, 15))
				.build();

		Set<ConstraintViolation<User>> violations = validator.validate(user);

		assertFalse(violations.isEmpty());
		assertThat(violations).hasSize(1);
		assertThat(violations).extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("Логин не должен содержать пробелы.");
	}

	@Test
	void userCreateFailBirthdayInFuture() throws Exception {
		User user = User.builder()
				.email("test@mail.com")
				.login("testUser")
				.name("Test Name")
				.birthday(LocalDate.of(2050, 5, 15))
				.build();

		Set<ConstraintViolation<User>> violations = validator.validate(user);

		assertFalse(violations.isEmpty());
		assertThat(violations).hasSize(1);
		assertThat(violations).extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("Дата рождения не может быть в будущем");
	}

	@Test
	void userUpdateFailIdNull() throws Exception {
		User updatedUser = User.builder().id(null)
				.email("updated@mail.com")
				.login("updatedUser")
				.name("Updated Name")
				.birthday(LocalDate.of(1990, 5, 15))
				.build();

		Set<ConstraintViolation<User>> violations = validator.validate(updatedUser, ValidationMarker.OnUpdate.class);

		assertFalse(violations.isEmpty());
		assertThat(violations).hasSize(1);
		assertThat(violations).extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("Не указан id");
	}

	@Test
	void userUpdateFailUnknownId() throws Exception {
		User user = User.builder().email("test@mail.com")
				.login("testUser")
				.name("Test Name")
				.birthday(LocalDate.of(1990, 5, 15))
				.build();

		controller.create(user);

		User updatedUser = User.builder()
				.id(20L) // Non-existing ID
				.email("updated@mail.com")
				.login("updatedUser")
				.name("Updated Name")
				.birthday(LocalDate.of(1990, 5, 15))
				.build();

		assertThrows(ValidationException.class, () -> {
			controller.update(updatedUser);
		}, "Пользователь не найден!");
	}

	@Test
	void userGetAll() throws Exception {
		User user1 = User.builder()
				.email("test1@mail.com")
				.login("testUser1")
				.name("Test Name 1")
				.birthday(LocalDate.of(1990, 5, 15))
				.build();

		User user2 = User.builder()
				.email("test2@mail.com")
				.login("testUser2")
				.name("Test Name 2")
				.birthday(LocalDate.of(1991, 6, 16))
				.build();

		controller.create(user1);
		controller.create(user2);

		List<User> users = controller.getAll().stream().toList();

		assertEquals(2, users.size());
	}
}
