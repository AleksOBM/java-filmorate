package ru.yandex.practicum.filmorate.dto.request.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DirectorUpdateRequest {

	@NotNull(message = "Id должен быть указан.")
	@Positive(message = "Id должен быть положительным числом.")
	Integer id;

	@Size(min = 2, max = 70, message = "Длина имени режиссера может быть от 2 до 70 символов.")
	@NotBlank(message = "Имя режиссера не может быть пустым.")
	String name;

	public boolean hasNname() {
		return name != null;
	}
}
