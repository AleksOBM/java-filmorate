package ru.yandex.practicum.filmorate.model;

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

	private Integer mpaId;

	private float rate;

//	@Builder.Default
//	private Set<Like> likes = new HashSet<>();

	@Builder.Default
	private Set<Integer> genreIds = new HashSet<>();

	@Builder.Default
	private Set<Integer> directorIds = new HashSet<>();

//	public float getRate() {
//		if (likes.isEmpty()) {
//			return 0f;
//		} else if (likes.size() == 1) {
//			return likes.iterator().next().getAssessment().getValue();
//		}
//		AtomicLong count = new AtomicLong();
//		AtomicLong sum = new AtomicLong();
//		likes.stream()
//				.map(Like::getAssessment)
//				.filter(assessment -> !assessment.equals(Assessment.UNDEFINED))
//				.map(Assessment::getValue).forEach(value -> {
//			sum.addAndGet(value);
//			count.getAndIncrement();
//		});
//
//		float average = (float) sum.get() / count.get();
//		rate = Math.round(average * 100) / 100.0f;
//		return rate;
//	}

	public void addGenreId(int genreId) {
		genreIds.add(genreId);
	}

	public void addDirectorId(int directorId) {
		directorIds.add(directorId);
	}

//	public void addLike(Like like) {
//		likes.add(like);
//	}
}
