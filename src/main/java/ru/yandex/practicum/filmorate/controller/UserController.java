package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.request.create.UserCreateRequest;
import ru.yandex.practicum.filmorate.dto.request.update.UserUpdateRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.servise.util.FriendsAction;
import ru.yandex.practicum.filmorate.servise.UserService;

import java.util.Collection;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public Collection<UserDto> findAll() {
		return userService.findAllUserDto();
	}

	@GetMapping("/{id}")
	public UserDto findById(@PathVariable long id) {
		return userService.findUserDtoById(id);
	}

	@GetMapping("/{id}/friends")
	public Collection<UserDto> getFriends(@PathVariable(name = "id") long userId) {
		return userService.getListOfFriends(userId);
	}

	@GetMapping("/{id}/friends/common/{otherId}")
	public Collection<UserDto> getMutualFriends(@PathVariable(name = "id") long userId, @PathVariable long otherId) {
		return userService.getListOfMutualFriends(userId, otherId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserDto create(@RequestBody @Valid UserCreateRequest userRequest) {
		return userService.create(userRequest);
	}

	@PutMapping
	public UserDto update(@RequestBody @Valid UserUpdateRequest userRequest) {
		return userService.update(userRequest);
	}

	@PutMapping("/{userId}/friends/{friendId}")
	public void addToFriends(@PathVariable long userId, @PathVariable long friendId) {
		userService.changeFriends(FriendsAction.REQUEST, userId, friendId);
	}

	@DeleteMapping("/{userId}/friends/{friendId}")
	public void removeFromFriends(@PathVariable long userId, @PathVariable long friendId) {
		userService.changeFriends(FriendsAction.REMOVE, userId, friendId);
	}
}
