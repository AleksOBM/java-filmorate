package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.request.create.FilmCreateRequest;
import ru.yandex.practicum.filmorate.dto.request.update.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.model.Assessment;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.util.LikeAction;

import java.util.Collection;

@RequestMapping("/films")
@RestController
@RequiredArgsConstructor
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

	@GetMapping("/common")
	public Collection<FilmDto> getCommonLikedFilms(@RequestParam(required = true) Long userId,
	                                               @RequestParam(required = true) Long friendId) {
		return filmService.getCommonLikedFilms(userId, friendId);
	}

	@GetMapping("/director/{directorId}")
	public Collection<FilmDto> findSortedFilmsOfDirector(
			@PathVariable Integer directorId,
			@RequestParam(required = false) String sortBy
	) {
		return filmService.getSortedFilmsOfDirector(directorId, sortBy);
	}

	@GetMapping("/search")
	public Collection<FilmDto> search(@RequestParam String query, @RequestParam String by) {
		return filmService.search(query, by);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public FilmDto create(@RequestBody @Valid FilmCreateRequest request) {
		return filmService.create(request);
	}

	@PutMapping
	public FilmDto update(@RequestBody FilmUpdateRequest request) {
		return filmService.update(request);
	}

	@PutMapping("/{filmId}/assessment/{assessment}/user/{userId}")
	public void setAssessment(@PathVariable long filmId,@PathVariable int assessment, @PathVariable long userId) {
		filmService.changeAssessment(LikeAction.SET, filmId, assessment, userId);
	}

	@PutMapping("/{filmId}/like/{userId}")
	public void setLike(@PathVariable long filmId, @PathVariable long userId) {
		filmService.changeLike(LikeAction.SET, filmId, userId);
	}

	@DeleteMapping("/{filmId}/like/{userId}")
	public void deleteLike(@PathVariable long filmId, @PathVariable long userId) {
		filmService.changeLike(LikeAction.REMOVE, filmId, userId);
	}

	@DeleteMapping("/{filmId}")
	public void deleteFilm(@PathVariable long filmId) {
		filmService.remove(filmId);
	}
}