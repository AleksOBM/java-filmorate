package ru.yandex.practicum.filmorate.exception;

public class AlredyAcceptedException extends RuntimeException {
	public AlredyAcceptedException(String message) {
		super(message);
	}
}
