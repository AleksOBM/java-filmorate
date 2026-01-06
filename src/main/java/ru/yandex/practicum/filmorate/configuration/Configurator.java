package ru.yandex.practicum.filmorate.configuration;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.filmorate.model.AgeRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.adapters.*;

import java.text.SimpleDateFormat;
import java.time.Duration;

@Configuration
public class Configurator {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		objectMapper.setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));

		SimpleModule module = new SimpleModule();
		module.addDeserializer(Duration.class, new DurationDeserializer());
		module.addSerializer(Duration.class, new DurationSerializer());
		module.addDeserializer(AgeRating.class, new AgeRatingDeserializer());
		module.addSerializer(AgeRating.class, new AgeRatingSerializer());
		module.addDeserializer(Genre.class, new GenreDeserializer());
		module.addSerializer(Genre.class, new GenreSerializer());

		objectMapper.registerModule(module);
		objectMapper.registerModule(new JavaTimeModule());

		return objectMapper;
	}
}



