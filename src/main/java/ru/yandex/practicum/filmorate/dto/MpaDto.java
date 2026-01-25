package ru.yandex.practicum.filmorate.dto;

import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Mpa;

@NoArgsConstructor
public class MpaDto extends Mpa {
	public MpaDto(int id, String name) {
		super(id, name);
	}

}
