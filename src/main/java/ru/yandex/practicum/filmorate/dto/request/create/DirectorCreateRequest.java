package ru.yandex.practicum.filmorate.dto.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DirectorCreateRequest {

	@Size(min = 2, max = 70, message = "Длина имени режиссера может быть от 2 до 70 символов.")
	@NotBlank(message = "Имя режиссера не может быть пустым.")
	String name;
}
