package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	InMemoryUserStorage userStorage;

	@Autowired
	InMemoryFilmStorage filmStorage;

	@AfterEach
	void setUp() {
		userStorage.reset();
		filmStorage.reset();
	}

	@Nested
	@DisplayName("Добавление пользователя.")
	class Create {

		@Test
		void userCreate() throws Exception {
			mockMvc.perform(post("/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\n" +
									"     \"name\": \"Cay Horstmann\",\n" +
									"     \"login\": \"horstman444\",\n" +
									"     \"email\": \"cay@horstmann.com\",\n" +
									"     \"birthday\": \"1959-06-16\"\n" +
									" }"
							))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id").value("1"))
					.andExpect(jsonPath("$.name").value("Cay Horstmann"))
					.andExpect(jsonPath("$.login").value("horstman444"))
					.andExpect(jsonPath("$.email").value("cay@horstmann.com"))
					.andExpect(jsonPath("$.birthday").value("1959-06-16"))
					.andExpect(jsonPath("$.friends").isArray());
		}

		@Test
		void userCreateFailEmail() throws Exception {
			mockMvc.perform(post("/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\n" +
									"  \"login\": \"dolore ullamco\",\n" +
									"  \"name\": \"\",\n" +
									"  \"email\": \"mail.ru\",\n" +
									"  \"birthday\": \"1980-08-20\"\n" +
									"}\n"
							))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.email").value(
							"Не распознан формат адреса электронной почты."
					));
		}

		@Test
		void userCreateFailLogin() throws Exception {
			mockMvc.perform(post("/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\n" +
									" \"login\": \"dolore ullamco\",\n" +
									" \"email\": \"yandex@mail.ru\",\n" +
									" \"birthday\": \"2446-08-20\"\n" +
									"}"
							))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.login").value(
							"Логин может состоять только из этих символов [A-Za-z0-9_]."
					));
		}

		@Test
		void userCreateFailBirthday() throws Exception {
			mockMvc.perform(post("/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\n" +
									"	\"login\": \"dolore\",\n" +
									"	\"name\": \"\",\n" +
									"	\"email\": \"test@mail.ru\",\n" +
									"	\"birthday\": \"2446-08-20\"\n" +
									" }"
							))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.birthday").value(
							"Дата рождения не может быть в будущем."
					));
		}
	}

	@Nested
	@DisplayName("Обновление пользователя.")
	class Update {

		@Test
		void userUpdate() throws Exception {

			mockMvc.perform(post("/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\n" +
							"	\"name\":\"Edmund Durgan\",\n" +
							"	\"login\":\"jfnC9siOkH\",\n" +
							"	\"email\":\"Samantha.Sporer4@hotmail.com\",\n" +
							"	\"birthday\":\"2005-12-17\"\n" +
							"}"
					));

			mockMvc.perform(put("/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\n" +
									"	\"id\":1,\n" +
									"	\"email\":\"mail@yandex.ru\",\n" +
									"	\"login\":\"doloreUpdate\",\n" +
									"	\"name\":\"est adipisicing\",\n" +
									"	\"birthday\":\"1976-09-20\"\n" +
									"}"
							))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").exists())
					.andExpect(jsonPath("$.email").exists())
					.andExpect(jsonPath("$.name").exists())
					.andExpect(jsonPath("$.login").exists())
					.andExpect(jsonPath("$.birthday").exists());
		}

		@Test
		void userUpdateUnknown() throws Exception {

			mockMvc.perform(put("/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\n" +
									"	\"login\":\"doloreUpdate\",\n" +
									"	\"name\":\"est adipisicing\",\n" +
									"	\"id\":4,\"email\":\"mail@yandex.ru\",\n" +
									"	\"birthday\":\"1976-09-20\"\n" +
									"}"
							))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.error").value(
							"Пользователь с id = 4 не найден."
					));
		}

	}

	@Nested
	@DisplayName("Получение пользователя.")
	class Get {

		@Test
		void usersGetAll() throws Exception {
			mockMvc.perform(post("/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\n" +
							"	\"name\":\"Samuel Ward\",\n" +
							"	\"login\":\"hi0LjgG5Jl\",\n" +
							"	\"email\":\"Adolf.Ruecker59@hotmail.com\",\n" +
							"	\"birthday\":\"1997-09-08\"\n" +
							"}"
					));

			mockMvc.perform(post("/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\n" +
							"	\"name\":\"Edmund Durgan\",\n" +
							"	\"login\":\"jfnC9siOkH\",\n" +
							"	\"email\":\"Samantha.Sporer4@hotmail.com\",\n" +
							"	\"birthday\":\"2005-12-17\"\n" +
							"}"
					));

			mockMvc.perform(get("/users"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.[*].id").exists())
					.andExpect(jsonPath("$.[0].id").value("1"));
		}
	}

	@Nested
	@DisplayName("Дружба пользователей.")
	class Friends {

		@Test
		void requestToFriend() throws Exception {
			mockMvc.perform(post("/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\n" +
							         "    \"name\": \"Cay Horstmann\",\n" +
							         "    \"login\": \"horstman444\",\n" +
							         "    \"email\": \"cay@horstmann.com\",\n" +
							         "    \"birthday\": \"1959-06-16\"\n" +
							         "}\n"
							))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.wantsFriends").exists())
					.andExpect(jsonPath("$.comesFriends").exists())
					.andExpect(jsonPath("$.friends").exists());

			mockMvc.perform(post("/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\n" +
							         "     \"name\": \"Herbert Schildt\",\n" +
							         "     \"login\": \"schildt\",\n" +
							         "     \"email\": \"Herb@herbschildt.com\",\n" +
							         "     \"birthday\": \"1951-02-28\",\n" +
							         "     \"friends\": null\n" +
							         " }\n"
							))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.wantsFriends").exists())
					.andExpect(jsonPath("$.comesFriends").exists())
					.andExpect(jsonPath("$.friends").exists());

			mockMvc.perform(patch("/users/1/friends/2/request"))
					.andExpect(status().isAccepted());

			mockMvc.perform(get("/users/1"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.wantsFriends.[0]").value("2"))
					.andExpect(jsonPath("$.comesFriends").isEmpty())
					.andExpect(jsonPath("$.friends").isEmpty());

			mockMvc.perform(get("/users/2"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.comesFriends.[0]").value("1"))
					.andExpect(jsonPath("$.wantsFriends").isEmpty())
					.andExpect(jsonPath("$.friends").isEmpty());
		}

		void friendAdd() throws Exception {
			mockMvc.perform(post("/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\n" +
							"	\"name\":\"Sherry Harris\",\n" +
							"	\"login\":\"E1NUneYUbD\",\n" +
							"	\"email\":\"Walton.Walter35@hotmail.com\",\n" +
							"	\"birthday\":\"1998-11-02\"\n" +
							"}"
					));

			mockMvc.perform(post("/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\n" +
							"	\"name\":\"Forrest Nienow\",\n" +
							"	\"login\":\"DkO5KK0Ssi\",\n" +
							"	\"email\":\"Talon_Prohaska44@yahoo.com\",\n" +
							"	\"birthday\":\"1986-11-28\"\n" +
							"}"
					));

			mockMvc.perform(put("/users/1/friends/2")
					.contentType(MediaType.APPLICATION_JSON));

			mockMvc.perform(get("/users/1/friends")
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].id").value("2"));

			mockMvc.perform(get("/users/2/friends")
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].id").value("1"));
		}

		void friendAddUnknownId() throws Exception {
			mockMvc.perform(put("/users/1/friends/3")
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.error").value(
							"Второй пользователь с id=3 не найден."
					));
		}

		void friendRemove() throws Exception {
			mockMvc.perform(delete("/users/1/friends/2")
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

			mockMvc.perform(get("/users/1/friends")
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].id").doesNotExist());

			mockMvc.perform(get("/users/2/friends")
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].id").doesNotExist());
		}

		void friendGetUnknownId() throws Exception {
			mockMvc.perform(get("/users/3/friends")
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.error").value(
							"Пользователь с id=3 не найден."
					));
		}

		void friendNotFriendRemove() {
		}

		void friendRemoveUnknownId() {
		}

		void friendRemoveUnknownFriendId() {
		}

		void friendGetCommonFriends() {
		}
	}


}

/*
		@ParameterizedTest
		@CsvSource({
		"1, I",
		"2, II",
		"3, III",
		"10, X",
		"20, XX",
		"11, XI",
		"200, CC",
		"732, DCCXXXII",
		"2275, MMCCLXXV",
		"999, CMXCIX",
		"444, CDXLIVI", // failure
		})
		void convertAll(int arabic, String roman) {
			assertEquals(roman, converter.toRoman(arabic));
		}
	 */