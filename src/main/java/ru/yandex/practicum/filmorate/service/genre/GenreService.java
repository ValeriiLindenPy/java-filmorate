package ru.yandex.practicum.filmorate.service.genre;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    /**
     * Retrieves all films
     *
     * @return {@link Collection<Genre>}
     */
    public Collection<Genre> getAll() {
        return genreStorage.getAll();
    }


    /**
     * Retrieves a genre by ID
     *
     * @param mpaId
     * @return {@link Genre}
     */
    public Genre getById(Long mpaId) {
        return genreStorage.getById(mpaId).orElseThrow(() ->
                new NotFoundException("Genre with ID %d not found".formatted(mpaId)));
    }
}
