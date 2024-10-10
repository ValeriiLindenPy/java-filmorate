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
import ru.yandex.practicum.filmorate.model.Film;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilmControllerTest {
    private Validator validator;
    private FilmController controller;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        controller = new FilmController();
    }

    @Test
    void filmCreate201() throws Exception {
        Film film = Film.builder().name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();

        Film filmCreated = controller.create(film);

        assertFalse(controller.getFilmsDB().isEmpty());

        Set<ConstraintViolation<Film>> violations = validator.validate(filmCreated);

        assertTrue(violations.isEmpty());

    }

    @Test
    void filmCreateFailNameBlack() throws Exception {
        Film film = Film.builder().name("")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();


        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Имя не может быть пустым.");
    }

    @Test
    void filmCreateFailNameNull() throws Exception {
        Film film = Film.builder().name(null)
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();


        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Имя не может быть пустым.");
    }

    @Test
    void filmCreateFailDescription() throws Exception {
        Film film = Film.builder().name("name")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                        "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им " +
                        "деньги, а именно 20 миллионов. " +
                        "о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Максимум для описания 200 символов.");
    }

    @Test
    void filmCreateFailDescriptionNull() throws Exception {
        Film film = Film.builder().name("name")
                .description(null)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Описание не может быть null.");
    }

    @Test
    void filmCreateFailReleaseDateNull() throws Exception {
        Film film = Film.builder().name("name")
                .description("description")
                .releaseDate(null)
                .duration(100).build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Дата выпуска не может быть null.");
    }

    @Test
    void filmCreateFailDurationNull() throws Exception {
        Film film = Film.builder().name("name")
                .description("description")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(null).build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Продолжительность не может быть null.");
    }

    @Test
    void filmCreateFailReleaseDate() throws Exception {
        Film film = Film.builder().name("name")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1890, 3, 25))
                .duration(100).build();

        assertThrows(ValidationException.class, () -> {
            controller.create(film);
        }, "Максимум для описания 200 символов.");
    }

    @Test
    void filmCreateFailDuration() throws Exception {
        Film film = Film.builder().name("name")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(-100).build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Продолжительность не может быть отрицательной.");

    }


    @Test
    void filmGetAll() throws Exception {
        Film film = Film.builder().name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();

        Film film2 = Film.builder().name("nisi eiusmod 2")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1969, 3, 25))
                .duration(120).build();

        controller.create(film);
        controller.create(film2);

        List<Film> films = controller.getAll().stream().toList();

        assertEquals(2, films.size());
    }


    @Test
    void filmUpdateFailId() throws Exception {
        Film updatedFilm = Film.builder().id(null).name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();

        Set<ConstraintViolation<Film>> violations = validator.validate(updatedFilm, ValidationMarker.OnUpdate.class);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Не указан id");
    }

    @Test
    void filmUpdateFailUnknown() throws Exception {
        Film film = Film.builder().name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();

        controller.create(film);

        Film updatedFilm = Film.builder().name("Heat")
                .id(20L).description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();

        assertThrows(ValidationException.class, () -> {
            controller.update(updatedFilm);
        }, "Фильм не найден!");
    }

    @Test
    void filmCreateMinReleaseDate() throws Exception {
        Film film = Film.builder().name("Valid name")
                .description("Valid description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(100).build();

        Film createdFilm = controller.create(film);

        assertNotNull(createdFilm);
        assertEquals(LocalDate.of(1895, 12, 28), createdFilm.getReleaseDate());
    }

    @Test
    void filmCreateFailDescriptionBlank() throws Exception {
        Film film = Film.builder().name("Valid name")
                .description("")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Описание не может быть null.");
    }

}
