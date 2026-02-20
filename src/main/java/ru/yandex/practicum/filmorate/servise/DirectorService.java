package ru.yandex.practicum.filmorate.servise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorStorage;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.request.create.DirectorCreateRequest;
import ru.yandex.practicum.filmorate.dto.request.update.DirectorUpdateRequest;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {
	private final DirectorStorage directorStorage;

	public DirectorDto getDirectorDtoById(int directorId) {
		log.info("Получение режиссера с id={}", directorId);
		return DirectorMapper.mapToDirectorDto(findDirector(directorId));
	}

	public Collection<DirectorDto> getAllDirectorDtos() {
		log.info("Получение всех режиссеров");
		return directorStorage.findAll().stream().map(DirectorMapper::mapToDirectorDto).toList();
	}

	public DirectorDto create(DirectorCreateRequest request) {
		log.info("Добавление нового режиссера name={}.", request.getName());
		Director director = DirectorMapper.mapToDirector(request);

		try {
			director = directorStorage.createDirector(director);
		} catch (DuplicateKeyException e) {
			throw new DuplicatedDataException("Этот режиссер " + director.getName() + " уже был добавлен ранее.");
		}

		return DirectorMapper.mapToDirectorDto(director);
	}

	public DirectorDto update(DirectorUpdateRequest request) {
		log.info("Обновление данных режиссера с id={}.", request.getId());
		Director oldDirector = findDirector(request.getId());
		Director newDirector = DirectorMapper.updateDirectorFields(oldDirector, request);

		try {
			newDirector = directorStorage.updateDirector(newDirector);
		} catch (DuplicateKeyException e) {
			throw new DuplicatedDataException(
					"Этот режиссер " + newDirector.getName() + " уже был добавлен ранее."
			);
		}

		return DirectorMapper.mapToDirectorDto(newDirector);
	}

	public void delete(int directorId) {
		log.info("Удаление режиссера с id={}", directorId);
		if (directorStorage.checkDirectorIsNotPresent(directorId)) {
			throw new NotFoundException("Режиссер с id=" + directorId + " не найден.");
		}
		directorStorage.deleteDirector(directorId);
	}

	private Director findDirector(int directorId) {
		return directorStorage.findById(directorId).orElseThrow(
				() -> new NotFoundException("Режиссер с id=" + directorId + " не найден.")
		);
	}

}
