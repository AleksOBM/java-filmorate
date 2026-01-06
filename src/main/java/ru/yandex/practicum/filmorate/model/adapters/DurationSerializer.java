package ru.yandex.practicum.filmorate.model.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Duration;

public class DurationSerializer extends StdSerializer<Duration> {

	public DurationSerializer() {
		super(Duration.class);
	}

	@Override
	public void serialize(
			Duration value,
			JsonGenerator generator,
			SerializerProvider provider
	) throws IOException {
		long minutes = value.toMinutes();
		generator.writeNumber(minutes);
	}
}
