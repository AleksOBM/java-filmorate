package ru.yandex.practicum.filmorate.model.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.yandex.practicum.filmorate.model.AgeRating;

import java.io.IOException;

public class AgeRatingSerializer extends StdSerializer<AgeRating> {

	public AgeRatingSerializer() {
		super(AgeRating.class);
	}

	@Override
	public void serialize(
			AgeRating value,
			JsonGenerator generator,
			SerializerProvider provider
	) throws IOException {
		generator.writeString(value.toString());
	}
}
