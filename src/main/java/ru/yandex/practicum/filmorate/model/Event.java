package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.service.util.EventType;
import ru.yandex.practicum.filmorate.service.util.Operation;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
	private Long eventId;
	private Long userId;
	private Instant timestamp;
	private EventType eventType;
	private Operation operation;
	private Long entityId;
}