package ru.yandex.practicum.filmorate.model.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.yandex.practicum.filmorate.model.Genre;

import java.io.IOException;

public class GenreSerializer extends StdSerializer<Genre> {

	public GenreSerializer() {
		super(Genre.class);
	}

	@Override
	public void serialize(
			Genre value,
			JsonGenerator generator,
			SerializerProvider provider
	) throws IOException {
		generator.writeString(value.toString());
	}
}
