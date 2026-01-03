package ru.yandex.practicum.filmorate.model.adapters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.yandex.practicum.filmorate.model.AgeRating;

import java.io.IOException;

public class AgeRatingDeserializer extends JsonDeserializer<AgeRating> {

	@Override
	public AgeRating deserialize(
			final JsonParser jsonParser,
			final DeserializationContext deserializationContext
	) throws IOException {
		String ageRatingString = jsonParser.readValueAs(String.class);
		return AgeRating.of(ageRatingString);
	}
}
