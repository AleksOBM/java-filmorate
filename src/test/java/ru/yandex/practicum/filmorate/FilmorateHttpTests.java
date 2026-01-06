package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
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


@SpringBootTest
@AutoConfigureMockMvc
class FilmorateHttpTests {

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

	@Test
	void users() throws Exception {
		userCreate();
		userCreateFailLogin();
		userCreateFailEmail();
		userCreateFailBirthday();
		userUpdate();
		userUpdateUnknown();
		usersGetAll();
	}

	@Test
	void friends() throws Exception {
		friendAdd();
		friendAddUnknownId();
		friendGetUnknownId();
		friendRemove();
		friendNotFriendRemove();
		friendRemoveUnknownId();
		friendRemoveUnknownFriendId();
		friendGetCommonFriends();
	}

	@Test
	void films() throws Exception {
		filmCreate();
		filmCreateFailName();
		filmCreateFailDescription();
		filmCreateFailReleaseDate();
		filmCreateFailDuration();
		filmUpdate();
		filmUpdateUnknown();
		filmGetAll();
		filmGetPopular();
		filmGetMatrix();
		filmCreateWithNotZeroRate();
	}

	@Test
	void likes() throws Exception {
		likeAdd();
		likeAddUnknownFilm();
		likeAddUnknownUser();
		likeRemove();
		likeRemoveUnknownFilm();
		likeRemoveUnknownUser();
	}

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

	private void userCreateFailEmail() throws Exception {
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
				.andExpect(jsonPath("$.error").value(
						"Некорректное значение параметра mail.ru: " +
								"Электронная почта должна содержать символ '@'."
				));
	}

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
				.andExpect(jsonPath("$.error").value(
						"Некорректное значение параметра dolore ullamco: " +
						"Логин не должен содержать пробелов."
				));
	}

	private void userCreateFailBirthday() throws Exception {
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
				.andExpect(jsonPath("$.error").value(
						"Некорректное значение параметра 2446-08-20: " +
						"Дата рождения не может быть в будущем."
				));
	}

	private void userUpdate() throws Exception {

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
						         "	\"id\":2,\n" +
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

	private void userUpdateUnknown() throws Exception {
		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\n" +
				         "	\"name\":\"Samuel Ward\",\n" +
				         "	\"login\":\"hi0LjgG5Jl\",\n" +
				         "	\"email\":\"Adolf.Ruecker59@hotmail.com\",\n" +
				         "	\"birthday\":\"1997-09-08\"\n" +
				         "}"
				));

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

	private void usersGetAll() throws Exception {
		mockMvc.perform(get("/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.[*].id").exists())
				.andExpect(jsonPath("$.[0].id").value("1"));
	}



	private void friendAdd() throws Exception {
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

	private void friendAddUnknownId() throws Exception {
		mockMvc.perform(put("/users/1/friends/3")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value(
						"Второй пользователь с id=3 не найден."
				));
	}

	private void friendRemove() throws Exception {
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

	private void friendGetUnknownId() throws Exception {
		mockMvc.perform(get("/users/3/friends")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value(
						"Пользователь с id=3 не найден."
				));
	}

	private void friendNotFriendRemove() {
	}

	private void friendRemoveUnknownId() {
	}

	private void friendRemoveUnknownFriendId() {
	}

	private void friendGetCommonFriends() {
	}


	void filmCreate() throws Exception {
		mockMvc.perform(post("/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\n" +
						         "  \"name\": \"Матрица\",\n" +
						         "  \"description\": \"Добро пожаловать в реальный мир\",\n" +
						         "  \"releaseDate\": \"1999-03-24\",\n" +
						         "  \"duration\": 136,\n" +
						         "  \"likes\": null\n" +
						         "}"
						))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Матрица"))
				.andExpect(jsonPath("$.description").value("Добро пожаловать в реальный мир"))
				.andExpect(jsonPath("$.releaseDate").value("1999-03-24"))
				.andExpect(jsonPath("$.duration").value("136"))
				.andExpect(jsonPath("$.likes").isArray())
				.andExpect(jsonPath("$.rate").value(0));
	}

	private void filmCreateFailName() {
	}

	private void filmCreateFailDescription() {
	}

	private void filmCreateFailReleaseDate() {
	}

	private void filmCreateFailDuration() {
	}

	private void filmUpdate() {
	}

	private void filmUpdateUnknown() {
	}

	private void filmGetAll() {
	}

	private void filmGetPopular() {
	}

	void filmGetMatrix() throws Exception {
		mockMvc.perform(get("/films/{id}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Матрица"))
				.andExpect(jsonPath("$.description").value("Добро пожаловать в реальный мир"))
				.andExpect(jsonPath("$.releaseDate").value("1999-03-24"))
				.andExpect(jsonPath("$.duration").value("136"))
				.andExpect(jsonPath("$.likes").isArray())
				.andExpect(jsonPath("$.rate").value(0));
	}

	private void filmCreateWithNotZeroRate() throws Exception {
		mockMvc.perform(post("/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\n" +
						         "  \"name\": \"Остров проклятых\",\n" +
						         "  \"description\": \"Некоторые места никогда не отпускают тебя\",\n" +
						         "  \"releaseDate\": \"2010-02-13\",\n" +
						         "  \"duration\": 138,\n" +
						         "  \"rate\": 4\n" +
						         "}"
						))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Остров проклятых"))
				.andExpect(jsonPath("$.description").value("Некоторые места никогда не отпускают тебя"))
				.andExpect(jsonPath("$.releaseDate").value("2010-02-13"))
				.andExpect(jsonPath("$.duration").value("138"))
				.andExpect(jsonPath("$.likes").isArray())
				.andExpect(jsonPath("$.rate").value(0));
	}



	private void likeAdd() {
	}

	private void likeAddUnknownFilm() {
	}

	private void likeAddUnknownUser() {
	}

	private void likeRemove() {
	}

	private void likeRemoveUnknownFilm() {
	}

	private void likeRemoveUnknownUser() {
	}
}




