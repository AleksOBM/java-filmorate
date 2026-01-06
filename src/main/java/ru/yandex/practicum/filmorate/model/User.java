package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@Jacksonized
public class User {

	private Long id;

	@Length(min = 2, max = 50, message = "Длина имени пользователя может быть от 2 до 50 символов.")
	private String name;

	@Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Логин может состоять только из этих символов [A-Za-z0-9_].")
	@Length(min = 3, max = 30, message = "Длина логина может быть от 3 до 20 символов.")
	private String login;

	@Email(message = "Не распознан формат адреса электронной почты.")
	private String email;

	@PastOrPresent(message = "Дата рождения не может быть в будущем.")
	private LocalDate birthday;

	@JsonProperty("wantsFriends")
	@Builder.Default
	private Set<Long> outgoingFriendRequests = new HashSet<>();

	@JsonProperty("comesFriends")
	@Builder.Default
	private Set<Long> incomingFriendRequests = new HashSet<>();

	@Builder.Default
	private Set<Long> rejectedRequests = new HashSet<>();

	@JsonProperty("friends")
	@Builder.Default
	private Set<Long> friendsIds = new HashSet<>();

	public void addOutgoingFriendRequest(Long id) {
		outgoingFriendRequests.add(id);

	}

	public void addIncomingFriendRequest(Long id) {
		incomingFriendRequests.add(id);

	}

	public void addRejectResponse(Long id) {
		rejectedRequests.add(id);

	}

	public void removeIncomingFriendRequest(Long id) {
		incomingFriendRequests.remove(id);
	}

	public void removeOutgoingFriendRequest(Long id) {
		outgoingFriendRequests.remove(id);

	}

	public void removeRejectResponse(Long id) {
		rejectedRequests.remove(id);
	}

	public void addFriendId(Long id) {
		friendsIds.add(id);
	}

	public void removeFriends(Long id) {
		friendsIds.remove(id);
	}

}
