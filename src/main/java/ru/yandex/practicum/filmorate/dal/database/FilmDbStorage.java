package ru.yandex.practicum.filmorate.dal.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;
import java.util.HashSet;

import static ru.yandex.practicum.filmorate.dal.database.sql.FilmQueryes.*;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

	@Autowired
	public FilmDbStorage(
			JdbcTemplate jdbc,
			@Qualifier("filmRowMapper") RowMapper<Film> mapper
	) {
		super(jdbc, mapper);
	}

	@Override
	@Transactional
	public Film createFilm(Film film) {
		long id = insertWithKeyHolder(
				SQL_FILMS_INSERT,
				film.getName(),
				film.getDescription(),
				film.getReleaseDate(),
				film.getDuration().toMinutes(),
				film.getMpaId()
		);

		film.setId(id);
		insertGenreIds(film);
		return film;
	}

	@Override
	public Film updateFilm(Film film) {
		updateWithControl(
				SQL_FILMS_UPDATE,
				film.getName(),
				film.getDescription(),
				film.getReleaseDate(),
				film.getDuration().toMinutes(),
				film.getMpaId(),
				film.getId()
		);
		return film;
	}

	@Override
	@Transactional
	public Optional<Film> findById(long filmId) {
		Optional<Film> filmOptional = findOneByIdInTable(filmId, "films");
		filmOptional.ifPresent(film -> film.setGenreIds(getGenreIdsByFilmId(filmId)));

		return filmOptional;
	}

	@Override
	public Collection<Film> findAll() {
		return findManyFilms(SQL_FILMS_FIND_ALL);
	}

	@Override
	public void setLike(long filmId, long userId) {
		updateWithControl(SQL_FILMS_SET_LIKE, filmId, userId);
	}

	@Override
	public void removeLike(long filmId, long userId) {
		updateWithControl(SQL_FILMS_DELETE_LIKE, filmId, userId);
	}

	@Override
	public Collection<Film> getTop(int top) {
		return findManyFilms(SQL_FILMS_FIND_TOP, top);
	}

	@Override
	public Collection<Film> getTopByFilters(Integer top, Integer genreId, String year) {
		Collection<Film> films;
		if (genreId != null && year != null && !year.isEmpty()) {
			films =  findManyByQuery(SQL_FILMS_FIND_TOP_BY_GENRE_AND_YEAR, Integer.parseInt(year), genreId, top);
		} else if (year != null && !year.isEmpty()) {
			films = findManyByQuery(SQL_FILMS_FIND_TOP_BY_YEAR, Integer.parseInt(year), top);
		} else if (genreId != null) {
			films = findManyByQuery(SQL_FILMS_FIND_TOP_BY_GENRES, genreId, top);
		} else {
			films = findManyByQuery(SQL_FILMS_FIND_TOP, top);
		}

		return films.stream().peek(film -> {
			Set<Integer> genres = getGenreIdsByFilmId(film.getId());
			film.setGenreIds(new HashSet<>(genres));
        }).toList();
	}

	@Override
	public boolean checkFilmIsNotPresent(Long filmId) {
		return checkIdIsNotPresentInTable(filmId, "films");
	}

	private void insertGenreIds(Film film) {
		Set<Integer> genreIds = film.getGenreIds();
		if (genreIds.isEmpty()) {
			return;
		}
		String placeholders = String.join(",",
				Collections.nCopies(genreIds.size(), " (" + film.getId() + ", ?)")
		);
		String sql = "INSERT INTO GENRES_OF_FILMS (FILM_ID, GENRE_ID) VALUES" + placeholders;
		updateWithControl(sql, genreIds.toArray());
	}

	private Collection<Film> findManyFilms(String query, Object... params) {
		return jdbc.query(query, rs -> {
			Map<Long, Film> films = new LinkedHashMap<>();
			AtomicInteger rowNum = new AtomicInteger();
			while (rs.next()) {
				long filmId = rs.getLong("id");
				Film film = films.computeIfAbsent(filmId, id -> {

					try {
						return rowMapper.mapRow(rs, rowNum.getAndIncrement());
					} catch (SQLException e) {
						throw new InternalServerException(
								"Не удалось получить все фильмы из базы.\n" + e.getMessage()
						);
					}
				});

				long userId = rs.getLong("user_id");
				if (!rs.wasNull() && film != null) {
					film.addLike(userId);
				}

				int genreId = rs.getInt("genre_id");
				if (!rs.wasNull() && film != null) {
					film.addGenreId(genreId);
				}
			}

			return new ArrayList<>(films.values());
		}, params);
	}

	private Set<Integer> getGenreIdsByFilmId(long filmId) {
		return findColumnByQuery(SQL_FILMS_FIND_GENREIDS_BY_FILM_ID, Integer.class, filmId);
	}

	@Override
	public Map<Long, Set<Long>> getAllLikes() {

		String sql = "SELECT user_id, film_id FROM likes";
		Map<Long, Set<Long>> allLikes = new HashMap<>();

		jdbc.query(sql, (rs) -> {
			long userId = rs.getLong("user_id");
			long filmId = rs.getLong("film_id");
			allLikes.computeIfAbsent(userId, k -> new HashSet<>()).add(filmId);
		});

		return allLikes;
	}
}
