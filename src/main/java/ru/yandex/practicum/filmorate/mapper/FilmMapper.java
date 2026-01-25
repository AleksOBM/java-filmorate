package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.request.create.FilmCreateRequest;
import ru.yandex.practicum.filmorate.dto.request.update.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

	public static Film mapToFilm(FilmCreateRequest request) {
		return Film.builder()
				.name(request.getName())
				.description(request.getDescription())
				.releaseDate(request.getReleaseDate())
				.duration(request.getDuration())
				.mpaId(request.getMpa().getId())
				.genreIds(request.getGenres().stream()
						.map(Genre::getId)
						.collect(Collectors.toSet()))
				.build();
	}

	public static FilmDto mapToFilmDto(Film film, Mpa mpa, Set<Genre> genres) {
		return FilmDto.builder()
				.id(film.getId())
				.name(film.getName())
				.description(film.getDescription())
				.releaseDate(film.getReleaseDate())
				.duration(film.getDuration())
				.likesCount(film.getLikesCount())
				.mpa(new MpaDto(mpa.getId(), mpa.getName()))
				.genres(genres.stream()
						.sorted(Comparator.comparing(Genre::getId))
						.map(genre -> new GenreDto(genre.getId(), genre.getName()))
						.toList())
				.build();
	}

	public static Film updateFilmFields(Film film, FilmUpdateRequest request) {

		if (request.hasNname()) {
			film.setName(request.getName());
		}

		if (request.hasDescription()) {
			film.setDescription(request.getDescription());
		}

		if (request.hasReleaseDate()) {
			film.setReleaseDate(request.getReleaseDate());
		}

		if (request.hasDuration()) {
			film.setDuration(request.getDuration());
		}

		if (request.hasMpa()) {
			film.setMpaId(request.getMpa().getId());
		}

		if (request.hasGenres()) {
			film.setGenreIds(request.getGenres().stream()
					.map(Genre::getId)
					.collect(Collectors.toSet()));
		}

		return film;
	}

}
