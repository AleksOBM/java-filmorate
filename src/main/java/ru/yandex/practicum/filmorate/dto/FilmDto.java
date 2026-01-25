package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Builder
@JsonPropertyOrder
public class FilmDto {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private long id;
	private String name;
	private String description;
	private LocalDate releaseDate;
	private Duration duration;

	@JsonProperty("rate")
	int likesCount;

	@Builder.Default
	private MpaDto mpa = new MpaDto();

	@Builder.Default
	private Collection<GenreDto> genres = new ArrayList<>();
}
