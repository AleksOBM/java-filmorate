package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.FriendsAction;
import ru.yandex.practicum.filmorate.servise.UserService;

import java.util.Collection;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public Collection<User> findAll() {
		return userService.findAll();
	}

	@GetMapping("/{id}")
	public User get(@PathVariable Long id) {
		return userService.get(id);
	}

	@GetMapping("/{id}/friends")
	public Collection<User> getFriends(@PathVariable(name = "id") Long userId) {
		return userService.getListOfFriends(userId);
	}

	@GetMapping("/{id}/friends/incoming")
	public Collection<User> getIncomingFriends(@PathVariable(name = "id") Long userId) {
		return userService.getIncomingFriends(userId);
	}

	@GetMapping("/{id}/friends/outgoing")
	public Collection<User> getOutgoingFriends(@PathVariable(name = "id") Long userId) {
		return userService.getOutgoingFriends(userId);
	}

	@GetMapping("/{id}/friends/rejected")
	public Collection<User> getRejectedFriends(@PathVariable(name = "id") Long userId) {
		return userService.getRejectedFriends(userId);
	}

	@GetMapping("/{id}/friends/common/{otherId}")
	public Collection<User> getMutualFriends(@PathVariable(name = "id") Long userId, @PathVariable Long otherId) {
		return userService.getListOfMutualFriends(userId, otherId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public User create(@RequestBody @Valid User user) {
		return userService.create(user);
	}

	@PutMapping
	public User update(@RequestBody @Valid User user) {
		return userService.update(user);
	}

	@PatchMapping("/{id}/friends/{friendId}/request")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void requestFriends(@PathVariable(name = "id") Long userId, @PathVariable Long friendId) {
		userService.changeFriends(FriendsAction.REQUEST, userId, friendId);
	}

	@PatchMapping("/{id}/friends/{friendId}/approve")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void approveFriends(@PathVariable(name = "id") Long userId, @PathVariable Long friendId) {
		userService.changeFriends(FriendsAction.APPROVE, userId, friendId);
	}

	@PatchMapping("/{id}/friends/{friendId}/reject")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void rejectFriends(@PathVariable(name = "id") Long userId, @PathVariable Long friendId) {
		userService.changeFriends(FriendsAction.REJECT, userId, friendId);
	}

	@PatchMapping("/{id}/friends/{friendId}/remove")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void removeFriends(@PathVariable(name = "id") Long userId, @PathVariable Long friendId) {
		userService.changeFriends(FriendsAction.REMOVE, userId, friendId);
	}
}
