package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.*;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Jacksonized
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String name;
    private String login;
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Builder.Default
    @JsonProperty("friends")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Set<Long> friendsIds = new HashSet<>();

    public void addFriendId(Long id) {
        friendsIds.add(id);
    }

    public void removeFriends(Long id) {
        friendsIds.remove(id);
    }
}
