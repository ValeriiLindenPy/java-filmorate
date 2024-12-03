package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;


import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> getAll() {
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director getByID(@PathVariable long id) {
        return directorService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director newDirector) {
        return directorService.update(newDirector);
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@PathVariable long id) {
        directorService.deleteById(id);
    }
}
