package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.servise.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
	private final GenreService genreService;

	@GetMapping("/{genreId}")
	public GenreDto getGenreById(@PathVariable long genreId) {
		return genreService.getGenreDtoById(genreId);
	}

	@GetMapping
	public Collection<GenreDto> getAllGenres() {
		return genreService.getAllGenreDtos();
	}
}
