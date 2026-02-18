package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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

	@Builder.Default
	private Set<Integer> genreIds = new HashSet<>();

	private Integer mpaId;

	@Builder.Default
	private Set<Integer> directorIds = new HashSet<>();

	@Builder.Default
	private Set<Long> likes = new HashSet<>();

	@JsonProperty(value = "rate")
	@Builder.Default
	private int likesCount = 0;

	public int getLikesCount() {
		likesCount = likes.size();
		return likesCount;
	}

	public void setLike(User user) {
		likes.add(user.getId());
	}

	public void removeLike(User user) {
		likes.remove(user.getId());
	}

	public void addGenreId(int genreId) {
		genreIds.add(genreId);
	}

	public void addDirectorId(int directorId) {
		directorIds.add(directorId);
	}

	public void addLike(long userId) {
		likes.add(userId);
	}
}
