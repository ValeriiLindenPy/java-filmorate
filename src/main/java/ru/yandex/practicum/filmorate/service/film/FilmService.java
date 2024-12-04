package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MPAStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final MPAStorage mpaStorage;
    private final GenreStorage genreStorage;
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    /**
     * Retrieves all films.
     *
     * @return {@link Collection<Film>}
     */
    public Collection<Film> getAll() {
        Map<Long, Set<Genre>> genres = genreStorage.getAllFilmsGenres();
        List<Film> films = filmStorage.getAll().stream().toList();
        setGenresForFilms(films);

        return films;
    }

    /**
     * Retrieves a film by its ID.
     *
     * @param id
     * @return {@link Film}
     * @throws NotFoundException
     */
    public Film getById(long id) {
        Film film = filmStorage.getById(id).orElseThrow(() ->
                new NotFoundException("Film with id %d not found".formatted(id)));
        film.setGenres(genreStorage.getFilmGenres(id));
        return film;
    }

    /**
     * Creates a new film.
     *
     * @param film
     * @return {@link Film}
     * @throws ValidationException
     */
    public Film create(Film film) {
        log.trace("Checking release date");
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.warn("The date {} is earlier than the minimum allowed date {}", film.getReleaseDate(), MIN_DATE);
            throw new ValidationException("The release date must not be earlier than December 28, 1895");
        }

        // Validate MPA rating
        if (film.getMpa() != null && mpaStorage.getById(film.getMpa().getId()).isEmpty()) {
            throw new ValidationException("Invalid MPA ID: " + film.getMpa().getId());
        }

        // Validate genres
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Long> availableGenreIds = genreStorage.getAll().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            Set<Long> filmGenreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            if (!availableGenreIds.containsAll(filmGenreIds)) {
                throw new ValidationException("Invalid film genre!");
            }
        }

        log.trace("Setting film ID");
        film.setId(generateId());
        log.trace("Adding film to storage");
        filmStorage.create(film);
        mpaStorage.saveMPA(film);
        genreStorage.saveGenres(film);
        return film;
    }

    /**
     * Updates an existing film.
     *
     * @param newFilm
     * @return {@link Film}
     * @throws NotFoundException
     */
    public Film update(Film newFilm) {
        log.debug("Checking existence of film with ID {}", newFilm.getId());
        if (filmStorage.getById(newFilm.getId()).isPresent()) {
            log.trace("Updating film in storage");
            filmStorage.update(newFilm);
            mpaStorage.updateMPA(newFilm);
            genreStorage.updateGenres(newFilm);
            return newFilm;
        }
        log.warn("Film with ID {} not found", newFilm.getId());
        throw new NotFoundException("Film not found!");
    }

    /**
     * Retrieves the top-rated films.
     *
     * @param count - int
     * @return {@link Collection<Film>}
     */
    public Collection<Film> getTop(int count) {
        Map<Long, Set<Genre>> genres = genreStorage.getAllFilmsGenres();
        List<Film> films = filmStorage.getTop(count).stream().toList();

        setGenresForFilms(films);
        return films;
    }

    /**
     * Get common films between the user and their friend, sorted by popularity
     *
     * @param userId - long
     * @param friendId - long
     * @return list of common films
     */
    public List<Film> getCommonFilms(long userId, long friendId) {
        List<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);
        setGenresForFilms(commonFilms);

        return commonFilms;
    }

    /**
     * Set genres for a film.
     * @param films
     */
    private void setGenresForFilms(List<Film> films) {
        Map<Long, Set<Genre>> filmGenres = genreStorage.getAllFilmsGenres();
        for (Film film : films) {
            Set<Genre> genres = filmGenres.getOrDefault(film.getId(), new HashSet<>());
            film.setGenres(genres);
        }
    }


    /**
     * Generates a new unique ID for a film.
     *
     * @return {@link Long}
     */
    private Long generateId() {
        Long currentId = filmStorage.getAll().stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        return ++currentId;
    }
}
