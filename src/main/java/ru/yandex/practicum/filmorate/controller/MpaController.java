package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.servise.MpaServise;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
	private final MpaServise mpaService;

	@GetMapping("/{mpaId}")
	public MpaDto getMpaById(@PathVariable long mpaId) {
		return mpaService.getMpaDtoById(mpaId);
	}

	@GetMapping
	public Collection<MpaDto> getAllMpa() {
		return mpaService.getAllMpaDtos();
	}
}
