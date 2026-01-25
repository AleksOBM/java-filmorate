package ru.yandex.practicum.filmorate.servise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaStorage;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaServise {

	private final MpaStorage mpaStorage;

	public MpaDto getMpaDtoById(long mpaId) {
		log.info("Получение рейтинга MPAA с id={}", mpaId);
		return mpaStorage.getMpaById(mpaId)
				.map(mpa ->  new MpaDto(mpa.getId(), mpa.getName()))
				.orElseThrow(() -> new NotFoundException("Рейтинг MPAA с id=" + mpaId + " не найден."));
	}

	public Collection<MpaDto> getAllMpaDtos() {
		log.info("Получение всех рейтингов MPAA");
		return mpaStorage.getAllMpa().stream()
				.map(mpa ->  new MpaDto(mpa.getId(), mpa.getName()))
				.toList();
	}
}
