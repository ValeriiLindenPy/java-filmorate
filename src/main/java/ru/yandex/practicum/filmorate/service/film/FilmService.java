package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.enums.FilmsSearchBy;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
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
    private final DirectorStorage directorStorage;
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    /**
     * Retrieves all films.
     *
     * @return {@link Collection<Film>}
     */
    public List<Film> getAll() {
        List<Film> films = filmStorage.getAll();
        setAdditionalFieldsForFilms(films);
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
        //validate film
        validateFilm(film);
        log.trace("Setting film ID");
        //set film id
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
            // update film
            filmStorage.update(newFilm);
            //update mpa
            mpaStorage.updateMPA(newFilm);
            //update genres
            genreStorage.updateGenres(newFilm);
            //update directors
            directorStorage.updateDirectors(newFilm);
            return newFilm;
        }
        log.warn("Film with ID {} not found", newFilm.getId());
        throw new NotFoundException("Film not found!");
    }

    /**
     * Retrieves the top-rated films.
     *
     * @param count   - int
     * @param genreId - Integer (nullable)
     * @param year    - Integer (nullable)
     * @return {@link List<Film>}
     */
    public List<Film> getTop(int count, Integer genreId, Integer year) {
        List<Film> films;

        if (genreId != null && year != null) {
            films = filmStorage.getTopYearAndGenre(count, genreId, year);
        } else if (genreId != null) {
            films = filmStorage.getTopByGenre(count, genreId);
        } else if (year != null) {
            films = filmStorage.getTopByYear(count, year);
        } else {
            films = filmStorage.getTop(count);
        }

        setGenresForFilms(films);
        return films;
    }

    public List<Film> getFilmsByDirectorSorted(Long directorId, String sortBy) {
        List<Film> films;
        if ("year".equalsIgnoreCase(sortBy)) {
            films = filmStorage.getDirectorFilmSortedByYear(directorId);
        } else if ("likes".equalsIgnoreCase(sortBy)) {
            films = filmStorage.getDirectorFilmSortedByLike(directorId);
        } else {
            throw new IllegalArgumentException("Invalid sortBy parameter");
        }
        setAdditionalFieldsForFilms(films);
        return films;
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
     * Set directors for films.
     * @param films
     */
    private void setDirectorsForFilms(List<Film> films) {
        Map<Long, Set<Director>> filmsDirectors = directorStorage.getAllFilmsDirectors();
        for (Film film : films) {
            Set<Director> directors = filmsDirectors.getOrDefault(film.getId(), new HashSet<>());
            film.setDirectors(directors);
        }
    }

    private void setAdditionalFieldsForFilms(List<Film> films) {
        setGenresForFilms(films);
        setDirectorsForFilms(films);
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

    /**
     * Validate Genres
     * @param film
     */
    private void validateFilmGenres(Film film) {
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
    }

    /**
     * Validate Directors
     * @param film
     */
    private void validateFilmDirector(Film film) {
        // Validate directors
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            Set<Long> availableDirectorIds = directorStorage.getAll().stream()
                    .map(Director::getId)
                    .collect(Collectors.toSet());

            Set<Long> filmDirectorsIds = film.getDirectors().stream()
                    .map(Director::getId)
                    .collect(Collectors.toSet());

            if (!availableDirectorIds.containsAll(filmDirectorsIds)) {
                throw new ValidationException("Invalid film director!");
            }
        }
    }

    /**
     * Validate MPA
     * @param film
     */
    private void validateFilmMPA(Film film) {
        // Validate MPA rating
        if (film.getMpa() != null && mpaStorage.getById(film.getMpa().getId()).isEmpty()) {
            throw new ValidationException("Invalid MPA ID: " + film.getMpa().getId());
        }
    }

    /**
     * Validate Release Date
     * @param film
     */
    private static void validateReleaseDate(Film film) {
        //Validate release date
        log.trace("Checking release date");
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.warn("The date {} is earlier than the minimum allowed date {}", film.getReleaseDate(), MIN_DATE);
            throw new ValidationException("The release date must not be earlier than December 28, 1895");
        }
    }

    /**
     * Validate a film
     * @param film
     */
    private void validateFilm(Film film) {
        // Validation methods:
        validateReleaseDate(film);
        validateFilmMPA(film);
        validateFilmGenres(film);
        validateFilmDirector(film);
    }


    public List<Film> search(String query, String searchBy) {
        if (query == null || (FilmsSearchBy.from(searchBy) == null)) {
            log.warn("Invalid search by parameter");
            throw new ValidationException("Invalid search by parameter");
        }
        List<Film> films = (filmStorage.searchByParam(query, FilmsSearchBy.from(searchBy)));
        setAdditionalFieldsForFilms(films);
        return films;
    }

    /**
     * Deletes a film and all related data by film ID.
     *
     * @param filmId ID of the film to be deleted
     * @throws NotFoundException if the film does not exist
     */
    public void deleteById(long filmId) {
        log.debug("Attempting to delete film with ID {}", filmId);
        if (filmStorage.getById(filmId).isEmpty()) {
            log.warn("Film with ID {} not found", filmId);
            throw new NotFoundException("Film with ID " + filmId + " not found");
        }
        log.trace("Deleting film ID {}", filmId);
        filmStorage.deleteById(filmId);
        log.info("Successfully deleted film with ID {}", filmId);
    }
}
