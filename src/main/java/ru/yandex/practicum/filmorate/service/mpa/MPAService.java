package ru.yandex.practicum.filmorate.service.mpa;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class MPAService {
    private final MPAStorage mpaStorage;

    /**
     * Retrieves all MPAs
     *
     * @return {@link Collection<MPA>}
     */
    public Collection<MPA> getAll() {
        return mpaStorage.getAll();
    }

    /**
     * Retrieves a MPA by ID
     *
     * @param mpaId
     * @return {@link MPA}
     */
    public MPA getById(Long mpaId) {
        return mpaStorage.getById(mpaId).orElseThrow(() ->
                new NotFoundException("MPA with ID %d not found".formatted(mpaId)));
    }
}
