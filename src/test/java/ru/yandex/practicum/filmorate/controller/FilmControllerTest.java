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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class FilmControllerTest {

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
	@DisplayName("Добавление фильма.")
	class Create {

		@Test
		void filmCreate() throws Exception {
			mockMvc.perform(post("/films")
							.contentType(MediaType.APPLICATION_JSON)
							.content("   {\n" +
									"    \"name\": \"Матрица\",\n" +
									"    \"description\": \"Добро пожаловать в реальный мир\",\n" +
									"    \"releaseDate\": \"1999-03-24\",\n" +
									"    \"duration\": 136,\n" +
									"    \"genres\": [\"drama\", \"comedy\"],\n" +
									"    \"age\": \"PG-13\",\n" +
									"    \"likes\": null\n" +
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

		@Test
		void filmCreateWithNotZeroRate() throws Exception {
			mockMvc.perform(post("/films")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\n" +
									"    \"name\": \"Остров проклятых\",\n" +
									"    \"description\": \"Некоторые места никогда не отпускают тебя\",\n" +
									"    \"releaseDate\": \"2010-02-13\",\n" +
									"    \"duration\": 138,\n" +
									"    \"genres\": [\"drama\", \"comedy\"],\n" +
									"    \"age\": \"NC-17\",\n" +
									"    \"rate\": 4\n" +
									"}\n"
							))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.name").value("Остров проклятых"))
					.andExpect(jsonPath("$.description").value("Некоторые места никогда не отпускают тебя"))
					.andExpect(jsonPath("$.releaseDate").value("2010-02-13"))
					.andExpect(jsonPath("$.duration").value("138"))
					.andExpect(jsonPath("$.likes").isArray())
					.andExpect(jsonPath("$.rate").value(0));
		}

		void filmCreateFailName() {
		}

		void filmCreateFailDescription() {
		}

		void filmCreateFailReleaseDate() {
		}

		void filmCreateFailDuration() {
		}
	}

	@Nested
	@DisplayName("Обновление фильма.")
	class Update {

		void filmUpdate() {
		}

		void filmUpdateUnknown() {
		}
	}

	@Nested
	@DisplayName("Получение фильма.")
	class Get {

		@Test
		void filmGetMatrix() throws Exception {
			mockMvc.perform(post("/films")
					.contentType(MediaType.APPLICATION_JSON)
					.content("   {\n" +
							"    \"name\": \"Матрица\",\n" +
							"    \"description\": \"Добро пожаловать в реальный мир\",\n" +
							"    \"releaseDate\": \"1999-03-24\",\n" +
							"    \"duration\": 136,\n" +
							"    \"genres\": [\"drama\", \"comedy\"],\n" +
							"    \"age\": \"PG-13\",\n" +
							"    \"likes\": null\n" +
							"}"
					));

			mockMvc.perform(get("/films/{id}", 1L))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.name").value("Матрица"))
					.andExpect(jsonPath("$.description").value("Добро пожаловать в реальный мир"))
					.andExpect(jsonPath("$.releaseDate").value("1999-03-24"))
					.andExpect(jsonPath("$.duration").value("136"))
					.andExpect(jsonPath("$.likes").isArray())
					.andExpect(jsonPath("$.rate").value(0));
		}

		void filmGetAll() {
		}

		void filmGetPopular() {
		}
	}

	@Nested
	@DisplayName("Лайки.")
	class Like {
		void likeAdd() {
		}

		void likeAddUnknownFilm() {
		}

		void likeAddUnknownUser() {
		}

		void likeRemove() {
		}

		void likeRemoveUnknownFilm() {
		}

		void likeRemoveUnknownUser() {
		}
	}

}
