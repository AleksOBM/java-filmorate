package ru.yandex.practicum.filmorate.controller.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.yandex.practicum.filmorate.exception.*;

import java.util.*;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler
	public ErrorResponse handleNotFound(final NotFoundException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ExceptionHandler
	public ErrorResponse handleNoContent(final NoContentException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler
	public ErrorResponse handleDuplicatedData(final DuplicatedDataException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.ALREADY_REPORTED)
	@ExceptionHandler
	public ErrorResponse handleAlredtAccepted(final AlredyAcceptedException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler
	public ErrorResponse handleNotAlloWed(final NotAllowedException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	@ExceptionHandler
	public ErrorResponse handleConditionsNotMet(final ConditionsNotMetException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler
	public ErrorResponse handleParameterNotValid(final ParameterNotValidException e) {
		return new ErrorResponse("Некорректное значение параметра " +
				e.getParameter() + ": " + e.getReason()
		);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler
	public ErrorResponse handleValidationException(final ValidationException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler
	public ErrorResponse handlerMethodValidationException(final HandlerMethodValidationException e) {
		return new ErrorResponse(e.getAllErrors().getFirst().getDefaultMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleMethodArgumentException(final MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();

		e.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		return errors;
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	public ErrorResponse handleAnyThrowable(final Throwable e) {
		UUID uuid = UUID.randomUUID();
		log.error("UUID={} Message=\"{}.\"", uuid, e.getMessage());
		return new ErrorResponse("Произошла непредвиденная ошибка. UUID=" + uuid);
	}
}

