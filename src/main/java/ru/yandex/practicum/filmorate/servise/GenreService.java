package ru.yandex.practicum.filmorate.servise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreStorage;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {
	private final GenreStorage genreStorage;

	public GenreDto getGenreDtoById(long genreId) {
		log.info("Получение жанра с id={}", genreId);
		return genreStorage.getGenreById(genreId)
				.map(genre ->  new GenreDto(genre.getId(), genre.getName()))
				.orElseThrow(() -> new NotFoundException("Жанр с id=" + genreId + " не найден."));
	}

	public Collection<GenreDto> getAllGenreDtos() {
		log.info("Получение всех жанров");
		return genreStorage.getAllGenres().stream()
				.map(genre ->  new GenreDto(genre.getId(), genre.getName()))
				.toList();
	}

}
