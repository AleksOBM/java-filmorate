package ru.yandex.practicum.filmorate.dal.rowmappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Assessment;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeRowMapper implements RowMapper<Like> {
	@Override
	public Like mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		return Like.builder()
				.id(resultSet.getLong("id"))
				.filmId(resultSet.getLong("film_id"))
				.userId(resultSet.getLong("user_id"))
				.assessment(Assessment.of(resultSet.getInt("assessment")))
				.build();
	}
}
