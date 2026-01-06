package ru.yandex.practicum.filmorate.model.adapters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.yandex.practicum.filmorate.model.Genre;

import java.io.IOException;

public class GenreDeserializer extends JsonDeserializer<Genre> {

	@Override
	public Genre deserialize(
			final JsonParser jsonParser,
			final DeserializationContext deserializationContext
	) throws IOException {
		String genreString = jsonParser.readValueAs(String.class);
		return Genre.of(genreString);
	}
}
