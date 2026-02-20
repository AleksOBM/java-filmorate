package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.request.create.DirectorCreateRequest;
import ru.yandex.practicum.filmorate.dto.request.update.DirectorUpdateRequest;
import ru.yandex.practicum.filmorate.model.Director;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DirectorMapper {
	public static Director mapToDirector(DirectorCreateRequest request) {
		return Director.builder()
				.name(request.getName())
				.build();
	}

	public static DirectorDto mapToDirectorDto(Director director) {
		return new DirectorDto(director.getId(), director.getName());
	}

	public static Director updateDirectorFields(Director director, DirectorUpdateRequest request) {
		if (request.hasNname()) {
			director.setName(request.getName());
		}
		return director;
	}

}
