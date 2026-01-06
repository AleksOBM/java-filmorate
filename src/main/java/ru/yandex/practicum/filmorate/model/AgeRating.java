package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import ru.yandex.practicum.filmorate.model.adapters.AgeRatingDeserializer;
import ru.yandex.practicum.filmorate.model.adapters.AgeRatingSerializer;

import java.util.Arrays;
import java.util.List;

@JsonSerialize(using = AgeRatingSerializer.class)
@JsonDeserialize(using = AgeRatingDeserializer.class)
public enum AgeRating {
	G("G", "у фильма нет возрастных ограничений"),
	PG("PG", "детям рекомендуется смотреть фильм с родителями"),
	PG13("PG-13", "детям до 13 лет просмотр не желателен"),
	R("R", "лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
	NC17("NC-17", "лицам до 18 лет просмотр запрещён"),
	UNEXPECTED("unexpected", "unexpected - примет любое другое значение через метод of()");

	private String value;

	@Getter
	private final String description;

	AgeRating(String value, String description) {
		this.value = value;
		this.description = description;
	}

	public static AgeRating of(String string) {
		AgeRating ageRating;
		for (AgeRating agr : AgeRating.values()) {
			if (agr.value.equals(string)) {
				return agr;
			}
		}
		ageRating = AgeRating.UNEXPECTED;
		ageRating.setValue(string);
		return ageRating;
	}

	private void setValue(String value) {
		this.value = value;
	}

	public static List<AgeRating> getValidAgeRatingList() {
		return Arrays.stream(AgeRating.values())
				.filter(ageRating -> !ageRating.equals(UNEXPECTED)).toList();
	}

	@Override
	public String toString() {
		return value;
	}
}
