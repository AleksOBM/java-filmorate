package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.request.create.FilmCreateRequest;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.request.update.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.servise.FilmService;
import ru.yandex.practicum.filmorate.servise.util.LikeAction;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDto findById(@PathVariable long id) {
        return filmService.findById(id);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getTop(@RequestParam(defaultValue = "10", required = false) Integer count,
                                      @RequestParam(required = false) Integer genreId,
                                      @RequestParam(required = false) String year) {
        return filmService.getTopFilms(count, genreId, year);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@RequestBody @Valid FilmCreateRequest request) {
        return filmService.create(request);
    }

    @PutMapping
    public FilmDto update(@RequestBody FilmUpdateRequest request) {
        return filmService.update(request);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void setLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.changeLike(LikeAction.SET, filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.changeLike(LikeAction.REMOVE, filmId, userId);
    }
}
