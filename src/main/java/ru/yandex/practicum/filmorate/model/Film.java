package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@Jacksonized
public class Film {

	private Long id;
	private String name;
	private String description;
	private LocalDate releaseDate;

	@Builder.Default
	private Duration duration = Duration.ZERO;

	private Integer mpaId;

	@Builder.Default
	private Float rate = 0.0f;

	@Builder.Default
	private Set<Integer> genreIds = new HashSet<>();

	@Builder.Default
	private Set<Integer> directorIds = new HashSet<>();

	public void addGenreId(int genreId) {
		genreIds.add(genreId);
	}

	public void addDirectorId(int directorId) {
		directorIds.add(directorId);
	}
}
