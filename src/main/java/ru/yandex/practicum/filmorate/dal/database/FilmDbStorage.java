package ru.yandex.practicum.filmorate.dal.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.dal.rowmappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Assessment;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.dal.database.sql.FilmQueryes.*;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

	@Autowired
	public FilmDbStorage(JdbcTemplate jdbc, @Qualifier("filmRowMapper") RowMapper<Film> mapper) {
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
				film.getMpaId(),
				0.0f
		);
		film.setId(id);
		insertGenreIds(film);
		insertDirectorIds(film);
		return film;
	}

	@Override
	@Transactional
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
		insertGenreIds(film);
		insertDirectorIds(film);
		return film;
	}

	@Override
	@Transactional
	public Optional<Film> findById(long filmId) {
		Optional<Film> filmOptional = findOneByIdInTable(filmId, "films");
		filmOptional.ifPresent(film -> {
			film.setGenreIds(getGenreIdsByFilmId(filmId));
			film.setDirectorIds(getDirectorIdsByFilmId(filmId));
			film.setRate(calculateRating(getLikesByFilmId(filmId)));
		});

		return filmOptional;
	}

	@Override
	public Collection<Film> findAll() {
		return findManyFilms(SQL_FILMS_FIND_ALL);
	}

	@Transactional
	@Override
	public void addLike(long filmId, long userId, Assessment assessment) {
		jdbc.update(
				SQL_FILMS_SET_LIKE,
				filmId,
				userId,
				assessment.getValue()
		);

		float rate = calculateRating(getLikesByFilmId(filmId));
		jdbc.update("UPDATE films SET rate = ? WHERE id = ?", rate, filmId);
	}

	@Override
	public void removeLike(long filmId, long userId) {
		updateWithControl(SQL_FILMS_DELETE_LIKE, filmId, userId);
	}

	@Override
	public Collection<Film> getALLFilmsOfDirector(Integer directorId) {
		return findManyFilms(SQL_FILMS_FIND_ALL_OF_DIRECTOR, directorId);
	}

	@Override
	public Collection<Film> getTop(int top) {
		return findManyFilms(SQL_FILMS_FIND_TOP, top);
	}

	@Override
	public Collection<Film> getTopByFilters(Integer top, Integer genreId, String year) {
		Collection<Film> films = List.of();
		if (genreId != null && year != null && !year.isBlank()) {
			films = findManyByQuery(SQL_FILMS_FIND_TOP_BY_GENRE_AND_YEAR, Integer.parseInt(year), genreId, top);
		} else if (year != null && !year.isBlank()) {
			films = findManyByQuery(SQL_FILMS_FIND_TOP_BY_YEAR, Integer.parseInt(year), top);
		} else if (genreId != null) {
			films = findManyByQuery(SQL_FILMS_FIND_TOP_BY_GENRES, genreId, top);
		}

		return films.stream().peek(film -> {
			Set<Integer> genres = getGenreIdsByFilmId(film.getId());
			film.setGenreIds(new HashSet<>(genres));
			Set<Integer> directors = getDirectorIdsByFilmId(film.getId());
			film.setDirectorIds(new HashSet<>(directors));
		}).toList();
	}

	@Override
	public boolean checkFilmIsNotPresent(Long filmId) {
		return checkIdIsNotPresentInTable(filmId, "films");
	}

	@Override
	public Collection<Film> getCommonLikedFilms(long userId, long friendId) {
		return findManyFilms(SQL_FILMS_FIND_COMMON_LIKED, userId, friendId);
	}

	@Override
	public Collection<Film> search(String query, String by) {
		String searchPattern = "%" + query.toLowerCase() + "%";
		String condition;
		if (by.contains("director") && by.contains("title")) {
			condition = "(LOWER(f.film_name) LIKE ? OR LOWER(d.director_name) LIKE ?)";
			return findManyFilms(String.format(SQL_FILMS_SEARCH, condition), searchPattern, searchPattern);
		} else if (by.contains("director")) {
			condition = "LOWER(d.director_name) LIKE ?";
			return findManyFilms(String.format(SQL_FILMS_SEARCH, condition), searchPattern);
		} else {
			condition = "LOWER(f.film_name) LIKE ?";
			return findManyFilms(String.format(SQL_FILMS_SEARCH, condition), searchPattern);
		}
	}

	@Override
	public void removeFilm(long filmId) {
		updateWithControl("DELETE FROM films WHERE id = ?", filmId);
	}

	@Override
	public Set<Like> getAllLikes() {
		String sql = "SELECT user_id, film_id, assessment FROM likes";
		Set<Like> allLikes = new HashSet<>();
		jdbc.query(sql, (rs) -> {
			long filmId = rs.getLong("film_id");
			long userId = rs.getLong("user_id");
			int assessment = rs.getInt("assessment");
			Like like = Like.builder().filmId(filmId).userId(userId).assessment(Assessment.of(assessment)).build();
			allLikes.add(like);
		});
		return allLikes;
	}

	@Override
	public Collection<Film> getFilmsByIds(Collection<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}

		String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
		String sql = String.format(SQL_FILMS_GET_BY_IDS, inSql);

		return findManyFilms(sql, ids.toArray());
	}

	private Set<Like> getLikesByFilmId(long filmId) {
		return jdbc.query(SQL_FILMS_FIND_LIKES_BY_FILM_ID, new LikeRowMapper(), filmId).stream()
				.collect(Collectors.toUnmodifiableSet());
	}

	private void insertGenreIds(Film film) {
		jdbc.update("DELETE FROM genres_of_films WHERE film_id = ?", film.getId());
		Set<Integer> genreIds = film.getGenreIds();
		if (genreIds == null || genreIds.isEmpty()) return;
		String placeholders = String.join(",", Collections.nCopies(genreIds.size(), " (" + film.getId() + ", ?)"));
		String sql = SQL_FILMS_INSERT_GENREIDS + placeholders;
		updateWithControl(sql, genreIds.toArray());
	}

	private void insertDirectorIds(Film film) {
		jdbc.update("DELETE FROM directors_of_films WHERE film_id = ?", film.getId());
		Set<Integer> directorIds = film.getDirectorIds();
		if (directorIds == null || directorIds.isEmpty()) return;
		String placeholders = String.join(",", Collections.nCopies(directorIds.size(), " (" + film.getId() + ", ?)"));
		String sql = SQL_FILMS_INSERT_DIRECTORIDS + placeholders;
		updateWithControl(sql, directorIds.toArray());
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
						throw new InternalServerException("Не удалось получить все фильмы из базы.\n" + e.getMessage());
					}
				});
				int genreId = rs.getInt("genre_id");
				if (!rs.wasNull() && film != null) film.addGenreId(genreId);
				int directorId = rs.getInt("director_id");
				if (!rs.wasNull() && film != null) film.addDirectorId(directorId);
			}
			return new ArrayList<>(films.values());
		}, params);
	}

	private Set<Integer> getGenreIdsByFilmId(long filmId) {
		return findColumnByQuery(SQL_FILMS_FIND_GENREIDS_BY_FILM_ID, Integer.class, filmId);
	}

	private Set<Integer> getDirectorIdsByFilmId(long filmId) {
		return findColumnByQuery(SQL_FILMS_FIND_DIRECTORIDS_BY_FILM_ID, Integer.class, filmId);
	}

	private float calculateRating(Set<Like> likes) {
		if (likes.isEmpty()) {
			return 0f;
		} else if (likes.size() == 1) {
			return likes.iterator().next().getAssessment().getValue();
		}
		AtomicLong count = new AtomicLong();
		AtomicLong sum = new AtomicLong();
		likes.stream()
				.map(Like::getAssessment)
				.filter(assessment -> !assessment.equals(Assessment.UNDEFINED))
				.map(Assessment::getValue).forEach(value -> {
					sum.addAndGet(value);
					count.getAndIncrement();
				});

		float average = (float) sum.get() / count.get();
		return Math.round(average * 100) / 100.0f;
	}
}