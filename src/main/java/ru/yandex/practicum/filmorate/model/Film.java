package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.time.DurationMin;
import ru.yandex.practicum.filmorate.model.adapters.*;

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

	@Length(max = 200, message = "Максимальная длина описания — 200 символов.")
	private String description;

	@Past(message = "Дата релиза должна быть в прошлом.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate releaseDate;

	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	@DurationMin(minutes = 0, message = "Продолжительность фильма должна быть положительным числом.")
	private Duration duration;

	@JsonSetter(nulls = Nulls.AS_EMPTY)
	@Builder.Default
	private Set<Genre> genres = new HashSet<>();

	@JsonProperty(value = "age")
	private AgeRating ageRating;

	@JsonProperty(value = "likes")
	@JsonSetter(nulls = Nulls.AS_EMPTY)
	@Builder.Default
	private Set<Long> whoLiked = new HashSet<>();

	@JsonProperty(value = "rate")
	private int likesCount;

	public int getLikesCount() {
		likesCount = whoLiked.size();
		return likesCount;
	}

	public void setLike(User user) {
		whoLiked.add(user.getId());
	}

	public void removeLike(User user) {
		whoLiked.remove(user.getId());
	}
}
