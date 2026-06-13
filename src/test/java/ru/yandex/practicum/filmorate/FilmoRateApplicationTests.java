package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.database.*;
import ru.yandex.practicum.filmorate.dal.rowmappers.*;
import ru.yandex.practicum.filmorate.dto.request.update.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Import({
		UserDbStorage.class, UserRowMapper.class,
		FilmDbStorage.class, FilmRowMapper.class,
		GenreDbStorage.class, GenreRowMapper.class,
		MpaDbStorage.class, MpaRowMapper.class,
		DirectorDbStorage.class, DirectorRowMapper.class,
		FilmMapper.class, UserMapper.class
})
@JdbcTest
@AutoConfigureTestDatabase
class FilmoRateApplicationTests {

	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;
	private final GenreDbStorage genreStorage;
	private final MpaDbStorage mpaStorage;
	private final DirectorDbStorage directorStorage;

	@Autowired
	FilmoRateApplicationTests(
			UserDbStorage userStorage,
			FilmDbStorage filmStorage,
			GenreDbStorage genreStorage,
			MpaDbStorage mpaStorage,
			DirectorDbStorage directorStorage
	) {
		this.userStorage = userStorage;
		this.filmStorage = filmStorage;
		this.genreStorage = genreStorage;
		this.mpaStorage = mpaStorage;
		this.directorStorage = directorStorage;
	}

	private String generateRandomText(int length, String addAfterStr, Character... addSynbols) {
		java.util.List<Character> symbols = new ArrayList<>(List.of(
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
				'u', 'v', 'w', 'x', 'y', 'z'
		));
		symbols.addAll(Arrays.asList(addSynbols));
		Random random = new Random();
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(symbols.size());
			sb.append(symbols.get(index));
		}

		return sb.append(addAfterStr).toString();
	}

	private User addFirstUser() {
		return userStorage.createUser(User.builder()
				.name("Test user")
				.email("testUser@mail.ru")
				.login("testUser")
				.birthday(LocalDate.parse("1999-01-01"))
				.build()
		);
	}

	private User addRandomUser() {
		return userStorage.createUser(User.builder()
				.name(generateRandomText(15, "", ' '))
				.email(generateRandomText(8, "@mail.ru"))
				.login(generateRandomText(8, ""))
				.birthday(LocalDate.parse("1999-01-01"))
				.build());
	}

	private Film addFifstFilm() {
		return filmStorage.createFilm(Film.builder()
				.name("First film")
				.description("First film description")
				.releaseDate(LocalDate.parse("1999-01-01"))
				.duration(Duration.ofMinutes(111))
				.mpaId(1)
				.genreIds(Set.of(1, 2))
				.build()
		);
	}

	private Film addRandomFilm() {
		return filmStorage.createFilm(Film.builder()
				.name(generateRandomText(20, "", ' '))
				.description(generateRandomText(50, ""))
				.releaseDate(LocalDate.parse("1999-01-01"))
				.duration(Duration.ofMinutes(150))
				.mpaId(1)
				.genreIds(Set.of(1, 2))
				.build());
	}

	private Director addRandomDirector() {
		return directorStorage.createDirector(
				Director.builder()
						.name(generateRandomText(20, "", ' '))
						.build()
		);
	}

	@Nested
	@DisplayName("Тест пользователей.")
	class Users {

		@Test
		public void testFindUserById() {
			long id = addFirstUser().getId();
			Optional<User> userOptional = userStorage.findById(id);

			assertThat(userOptional)
					.isPresent()
					.hasValueSatisfying(user -> {
								assertThat(user).hasFieldOrPropertyWithValue("id", id);
								assertThat(user).hasFieldOrPropertyWithValue("name", "Test user");
								assertThat(user).hasFieldOrPropertyWithValue("login", "testUser");
								assertThat(user).hasFieldOrPropertyWithValue(
										"birthday", LocalDate.parse("1999-01-01")
								);
								assertThat(user).hasFieldOrPropertyWithValue(
										"friendsIds", HashSet.newHashSet(0)
								);
							}
					);
		}

		@Test
		public void testFindAllUsers() {
			User user1 = addRandomUser();
			User user2 = addRandomUser();
			User user3 = addRandomUser();
			User user4 = addRandomUser();
			User user5 = addRandomUser();

			Collection<User> users = userStorage.findAll();

			assertThat(users)
					.hasSize(5)
					.hasSameElementsAs(List.of(user1, user2, user3, user4, user5));
		}

		@Test
		public void updateUser() {
			long id = addRandomUser().getId();

			User user = User.builder()
					.id(id)
					.name("Updated user")
					.login("updatedUser")
					.email("updatedUser@mail.ru")
					.birthday(LocalDate.parse("1999-01-01"))
					.build();

			userStorage.updateUser(user);
			User updatedUser = userStorage.findById(id).orElse(User.builder().build());

			assertThat(updatedUser).isEqualTo(user);
		}

		@Test
		public void friends() {
			long id1 = addRandomUser().getId();
			long id2 = addRandomUser().getId();
			long id3 = addRandomUser().getId();

			userStorage.addFriend(id1, id2);
			userStorage.addFriend(id2, id1);

			userStorage.addFriend(id2, id3);
			userStorage.addFriend(id3, id2);

			userStorage.addFriend(id3, id1);
			userStorage.addFriend(id1, id3);

			assertThat(userStorage.getFriendIds(id1)).isEqualTo(Set.of(id2, id3));
			assertThat(userStorage.getFriendIds(id2)).isEqualTo(Set.of(id1, id3));

			userStorage.removeFriend(id1, id2);
			assertThat(userStorage.getFriendIds(id1)).isEqualTo(Set.of(id3));
			assertThat(userStorage.getFriendIds(id2)).isEqualTo(Set.of(id1, id3));
		}

		@Test
		void recomendations() {
			User user1 = addRandomUser();
			User user2 = addRandomUser();
			User user3 = addRandomUser();

			Film film1 = addRandomFilm();
			Film film2 = addRandomFilm();
			Film film3 = addRandomFilm();

			filmStorage.addLike(film1.getId(), user1.getId(), Assessment.SEVEN);
			filmStorage.addLike(film2.getId(), user2.getId(), Assessment.EIGHT);
			filmStorage.addLike(film3.getId(), user3.getId(), Assessment.NINE);


		}

	}

	@Nested
	@DisplayName("Тест фильмов.")
	class Films {

		@Test
		public void testFindFilmById() {
			long id = addFifstFilm().getId();
			Optional<Film> filmOptional = filmStorage.findById(id);

			assertThat(filmOptional)
					.isPresent()
					.hasValueSatisfying(film -> {
								assertThat(film).hasFieldOrPropertyWithValue("id", id);
								assertThat(film).hasFieldOrPropertyWithValue("name", "First film");
								assertThat(film).hasFieldOrPropertyWithValue(
										"description", "First film description"
								);
								assertThat(film).hasFieldOrPropertyWithValue(
										"releaseDate", LocalDate.parse("1999-01-01")
								);
								assertThat(film).hasFieldOrPropertyWithValue(
										"duration", Duration.ofMinutes(111)
								);
								assertThat(film).hasFieldOrPropertyWithValue("mpaId", 1);
								assertThat(film).hasFieldOrPropertyWithValue("genreIds", Set.of(1, 2));
								assertThat(film).hasFieldOrPropertyWithValue("rate", 0.0f);
							}
					);
		}

		@Test
		public void testFindAllFilms() {
			Film film1 = addRandomFilm();
			Film film2 = addRandomFilm();
			Film film3 = addRandomFilm();
			Film film4 = addRandomFilm();
			Film film5 = addRandomFilm();

			Collection<Film> films = filmStorage.findAll();

			assertThat(films)
					.hasSize(5)
					.hasSameElementsAs(List.of(film1, film2, film3, film4, film5));
		}

		@Test
		public void updateFilm() {
			long id = addRandomFilm().getId();

			Film film = Film.builder()
					.id(id)
					.name("Updated film")
					.description("updatedFilm")
					.releaseDate(LocalDate.parse("1999-01-01"))
					.duration(Duration.ofMinutes(111))
					.mpaId(1)
					.genreIds(Set.of(1, 2))
					.build();

			filmStorage.updateFilm(film);
			Film updatedFilm = filmStorage.findById(id).orElse(Film.builder().build());

			assertThat(updatedFilm).isEqualTo(film);
		}

		@Test
		void likes() {
			Film film1 = addRandomFilm();

			User user1 = addRandomUser();
			User user2 = addRandomUser();
			User user3 = addRandomUser();

			filmStorage.addLike(film1.getId(), user1.getId(), Assessment.ONE);
			filmStorage.addLike(film1.getId(), user2.getId(), Assessment.of(3));
			filmStorage.addLike(film1.getId(), user3.getId(), Assessment.of(null));

			film1 = filmStorage.findById(film1.getId()).orElse(null);
			Assertions.assertNotNull(film1);
			assertThat(film1.getRate()).isEqualTo(4.67f);

			filmStorage.removeLike(film1.getId(), user1.getId());

			film1 = filmStorage.findById(film1.getId()).orElse(null);
			Assertions.assertNotNull(film1);
			assertThat(film1.getRate()).isEqualTo(6.5f);
		}

		@Test
		void genres() {
			Film film1 = addRandomFilm();

			Map<Integer, Genre> genres = genreStorage.getMapOfAllGenres();

			FilmUpdateRequest request = new FilmUpdateRequest();
			request.setGenres(new HashSet<>(genres.values()));
			FilmMapper.updateFilmFields(film1, request);

			film1 = filmStorage.updateFilm(film1);

			Collection<Integer> genreIds = genreStorage.getAllGenres().stream().map(Genre::getId).toList();

			assertThat(genreIds).containsAll(film1.getGenreIds());
		}

		@Test
		void mpa() {
			Film film1 = addRandomFilm();

			Map<Integer, Mpa> mpas = mpaStorage.getMapOfAllMpa();

			FilmUpdateRequest request = new FilmUpdateRequest();
			request.setMpa(mpas.get(4));
			FilmMapper.updateFilmFields(film1, request);

			film1 = filmStorage.updateFilm(film1);

			Collection<Integer> mpaIds = mpaStorage.getAllMpa().stream().map(Mpa::getId).toList();

			assertThat(mpaIds).containsAll(film1.getGenreIds());

		}

		@Test
		void top() {

			User user1 = addRandomUser();

			Film film1 = addRandomFilm();
			Film film2 = addRandomFilm();
			Film film3 = addRandomFilm();

			Director director = addRandomDirector();
			film1.setDirectorIds(Set.of(director.getId()));
			film2.setDirectorIds(Set.of(director.getId()));
			film3.setDirectorIds(Set.of(director.getId()));

			film1 = filmStorage.updateFilm(film1);
			film2 = filmStorage.updateFilm(film2);
			film3 = filmStorage.updateFilm(film3);

			filmStorage.addLike(film1.getId(),  user1.getId(), Assessment.ONE);
			filmStorage.addLike(film2.getId(),  user1.getId(), Assessment.NINE);
			filmStorage.addLike(film3.getId(),  user1.getId(), Assessment.SIX);

			assertThat(filmStorage.findById(film1.getId()).orElseThrow().getRate()).isEqualTo(1);
			assertThat(filmStorage.findById(film2.getId()).orElseThrow().getRate()).isEqualTo(9);
			assertThat(filmStorage.findById(film3.getId()).orElseThrow().getRate()).isEqualTo(6);

			Collection<Film> films = filmStorage.getTop(2);

			assertThat(films).containsExactly(film2, film3);

		}

	}

}