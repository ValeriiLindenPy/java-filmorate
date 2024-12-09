package ru.yandex.practicum.filmorate.storage.film;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.FilmsSearchBy;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.MPAStorage;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.MPARowMapper;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, MPAStorage.class, MPARowMapper.class,
        LikeStorage.class, DirectorStorage.class, DirectorRowMapper.class})
@AutoConfigureTestDatabase
public class FilmDbStorageTest {
    private final MPAStorage mpaStorage;
    private final FilmDbStorage filmStorage;

    @Test
    public void testGetById() {
        Optional<Film> filmOptional = filmStorage.getById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId()).isEqualTo(1L);
                    assertThat(film.getName()).isEqualTo("The Matrix");
                    assertThat(film.getDescription()).isEqualTo("A computer hacker learns the truth about his reality.");
                    assertThat(film.getDuration()).isEqualTo(136);
                    assertThat(film.getReleaseDate()).isEqualTo(LocalDate.parse("1999-03-31"));
                    assertThat(film.getMpa().getId()).isEqualTo(1L);
                    assertThat(film.getMpa().getName()).isEqualTo("G");
                });
    }

    @Test
    public void testGetAll() {
        List<Film> films = filmStorage.getAll();

        assertThat(films).hasSize(5);
    }

    @Test
    public void testCreate() {
        Film film = new Film();
        film.setId(6L);
        film.setName("New Film");
        film.setDescription("Description of new film");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setMpa(new MPA(1L, "G"));


        filmStorage.create(film);
        mpaStorage.saveMPA(film);
        Optional<Film> createdFilm = filmStorage.getById(6L);

        assertThat(createdFilm)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getName()).isEqualTo("New Film");
                    assertThat(f.getDescription()).isEqualTo("Description of new film");
                    assertThat(f.getDuration()).isEqualTo(120);
                    assertThat(f.getReleaseDate()).isEqualTo(LocalDate.of(2023, 1, 1));
                    assertThat(f.getMpa().getId()).isEqualTo(1L);
                    assertThat(f.getMpa().getName()).isEqualTo("G");
                });
    }

    @Test
    public void testUpdate() {
        Optional<Film> optionalFilm = filmStorage.getById(1L);
        assertThat(optionalFilm).isPresent();

        Film film = optionalFilm.get();
        film.setName("Updated Name");
        film.setDescription("Updated Description");
        film.setDuration(150);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        filmStorage.update(film);

        Optional<Film> updatedFilm = filmStorage.getById(1L);

        assertThat(updatedFilm)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getName()).isEqualTo("Updated Name");
                    assertThat(f.getDescription()).isEqualTo("Updated Description");
                    assertThat(f.getDuration()).isEqualTo(150);
                    assertThat(f.getReleaseDate()).isEqualTo(LocalDate.of(2000, 1, 1));
                });
    }

    @Test
    public void testDeleteById() {
        Film film = new Film();
        film.setId(6L);
        film.setName("New Film");
        film.setDescription("Description of new film");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setMpa(new MPA(1L, "G"));

        filmStorage.create(film);
        filmStorage.deleteById(6L);

        Optional<Film> optionalFilm = filmStorage.getById(6L);

        assertThat(optionalFilm).isNotPresent();
    }


    @Test
    public void testGetTop() {

        List<Film> topFilms = filmStorage.getTop(2);

        assertThat(topFilms).hasSize(2);

        Film topFilm = topFilms.iterator().next();

        assertThat(topFilm.getId()).isEqualTo(2L);
    }

    @Test
    public void testGetCommonFilms() {
        List<Film> commonFilms = filmStorage.getCommonFilms(1L, 2L);

        assertThat(commonFilms).hasSize(1);

        Film commonFilm = commonFilms.getFirst();
        assertThat(commonFilm.getName()).isEqualTo("Inception");
        assertThat(commonFilm.getId()).isEqualTo(2L);
    }

    @Test
    public void testDirectorFilmsByLikes() {
        List<Film> directorFilms = filmStorage.getDirectorFilmSortedByLike(1L);
        assertThat(directorFilms).hasSize(2);
        assertThat(directorFilms).first().isEqualTo(filmStorage.getById(2L).get());
    }

    @Test
    public void testDirectorFilmsByYear() {
        List<Film> directorFilms = filmStorage.getDirectorFilmSortedByYear(1L);
        assertThat(directorFilms).hasSize(2);
        assertThat(directorFilms).first().isEqualTo(filmStorage.getById(1L).get());
    }

    @Test
    public void testGetTopYearAndGenre() {
        List<Film> topFilmsByYearAndGenre = filmStorage.getTopYearAndGenre(1, 6, 1999);

        assertThat(topFilmsByYearAndGenre)
                .isNotNull()
                .hasSize(1);

        Film topFilm = topFilmsByYearAndGenre.iterator().next();
        assertThat(topFilm.getId()).isEqualTo(1L);
        assertThat(topFilm.getReleaseDate().getYear()).isEqualTo(1999);
    }

    @Test
    public void searchByParam() {
        List<Film> films = filmStorage.searchByParam("matrix", FilmsSearchBy.TITLE);
        assertThat(films).hasSize(1);
        assertThat(films).first().isEqualTo(filmStorage.getById(1L).get());

        films = filmStorage.searchByParam("ron", FilmsSearchBy.DIRECTOR);
        assertThat(films).hasSize(1);
        assertThat(films).first().isEqualTo(filmStorage.getById(1L).get());

        films = filmStorage.searchByParam("on", FilmsSearchBy.ALL);
        assertThat(films).hasSize(2);
        assertThat(films).first().isEqualTo(filmStorage.getById(2L).get());
        assertThat(films).last().isEqualTo(filmStorage.getById(1L).get());
    }
}
