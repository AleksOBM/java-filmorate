package ru.yandex.practicum.filmorate.dal.database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("directorDbStorage")
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {

	public DirectorDbStorage(
			JdbcTemplate jdbc,
			@Qualifier("directorRowMapper") RowMapper<Director> rowMapper
	) {
		super(jdbc, rowMapper);
	}

	@Override
	public Optional<Director> findById(long id) {
		return findOneByIdInTable(id, "directors");
	}

	@Override
	public Collection<Director> findAll() {
		return findAllInTable("directors");
	}

	@Override
	public Director createDirector(Director director) {
		int id = (int) insertWithKeyHolder(
				"INSERT INTO directors (director_name) VALUES (?)",
				director.getName()
		);
		director.setId(id);
		return director;
	}

	@Override
	public Director updateDirector(Director director) {
		updateWithControl(
				"UPDATE directors SET director_name= ? WHERE id = ?",
				director.getName(),
				director.getId()
		);
		return director;
	}

	@Override
	public void deleteDirector(Integer directorId) {
		updateWithControl(
				"DELETE FROM directors WHERE id = ?",
				directorId
		);
	}

	public boolean checkDirectorIsNotPresent(int directorId) {
		return checkIdIsNotPresentInTable(directorId, "directors");
	}

}
