package ru.yandex.practicum.filmorate.servise;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class IdentifyService {
	public  <T> long getNextId(Map<Long, T> map) {
		log.trace("Генерация нового id");
		int currentMaxId = map.keySet()
				.stream()
				.mapToInt(Math::toIntExact)
				.max()
				.orElse(0);
		return ++currentMaxId;
	}
}
