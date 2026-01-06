package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.yandex.practicum.filmorate.model.adapters.GenreDeserializer;
import ru.yandex.practicum.filmorate.model.adapters.GenreSerializer;

import java.util.Arrays;
import java.util.List;

@JsonSerialize(using = GenreSerializer.class)
@JsonDeserialize(using = GenreDeserializer.class)
public enum Genre {
	ACTION("action"),
	ADVENTURE("adventure"),
	ANIMATED("animated"),
	COMEDY("comedy"),
	DRAMA("drama"),
	FANTASY("fantasy"),
	HISTORICAL("historical"),
	HORROR("horror"),
	MELODRAMA("melodrama"),
	TRILLER("triller"),
	UNEXPECTED("unexpected");

	private String value;

	Genre(String value) {
		this.value = value;
	}

	public static Genre of(String string) {
		Genre genre;
		for (Genre gnr : Genre.values()) {
			if (gnr.value.equals(string.toLowerCase())) {
				return gnr;
			}
		}
		genre = Genre.UNEXPECTED;
		genre.setValue(string);
		return genre;
	}

	private void setValue(String value) {
		this.value = value;
	}

	public static List<Genre> getValidGenresList() {
		return Arrays.stream(Genre.values()).filter(genre -> !genre.equals(UNEXPECTED)).toList();
	}

	@Override
	public String toString() {
		return value;
	}
}
