package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;
import ru.yandex.practicum.filmorate.model.adapters.DurationDeserializer;
import ru.yandex.practicum.filmorate.model.adapters.DurationSerializer;

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

	@NotEmpty(message = "Название не может быть пустым.")
	private String name;
	private String description;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate releaseDate;

	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	private Duration duration;

	@Builder.Default
	@JsonProperty("likes")
	@JsonSetter(nulls = Nulls.AS_EMPTY)
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
