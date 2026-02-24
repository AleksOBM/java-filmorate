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
import java.util.concurrent.atomic.AtomicLong;

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
	private int likesCount;

	@Builder.Default
	private Set<Like> assessments = new HashSet<>();

	@JsonProperty(value = "assessment")
	private float averageAssessment;

	public float getAverageAssessment() {
		if (assessments.isEmpty()) {
			return 0f;
		} else if (assessments.size() == 1) {
			return assessments.iterator().next().getAssessment().getValue();
		}
		AtomicLong count = new AtomicLong();
		AtomicLong sum = new AtomicLong();
		assessments.stream()
				.map(Like::getAssessment)
				.filter(assessment -> !assessment.equals(Assessment.UNDEFINED))
				.map(Assessment::getValue).forEach(value -> {
			sum.addAndGet(value);
			count.getAndIncrement();
		});

		float average = (float) sum.get() / count.get();
		averageAssessment = Math.round(average * 100) / 100.0f;
		return averageAssessment;
	}

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
