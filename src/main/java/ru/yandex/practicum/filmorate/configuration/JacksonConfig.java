package ru.yandex.practicum.filmorate.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.filmorate.model.adapters.*;

import java.time.Duration;

@Configuration
public class JacksonConfig {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Duration.class, new DurationDeserializer());
		module.addSerializer(Duration.class, new DurationSerializer());
		objectMapper.registerModule(module);
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}
}

