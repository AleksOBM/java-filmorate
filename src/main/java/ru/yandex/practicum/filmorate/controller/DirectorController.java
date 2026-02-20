package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.request.create.DirectorCreateRequest;
import ru.yandex.practicum.filmorate.dto.request.update.DirectorUpdateRequest;
import ru.yandex.practicum.filmorate.servise.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
	private final DirectorService directorService;

	@GetMapping
	public Collection<DirectorDto> getAllDirectors() {
		return directorService.getAllDirectorDtos();
	}

	@GetMapping("/{directorId}")
	public DirectorDto getDirectorById(@PathVariable Integer directorId) {
		return directorService.getDirectorDtoById(directorId);
	}

	@PostMapping
	public DirectorDto createDirector(@RequestBody @Valid DirectorCreateRequest directorRequest) {
		return directorService.create(directorRequest);
	}

	@PutMapping
	public DirectorDto updateDirector(@RequestBody @Valid DirectorUpdateRequest directorRequest) {
		return directorService.update(directorRequest);
	}

	@DeleteMapping("/{directorId}")
	public void deleteDirector(@PathVariable Integer directorId) {
		directorService.delete(directorId);
	}
}
