package ru.yandex.practicum.filmorate.dal.rowmappers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Qualifier("genreRowMapper")
public class GenreRowMapper implements RowMapper<Genre> {

	@Override
	public Genre mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		return new Genre(
				resultSet.getInt("id"),
				resultSet.getString("genre_name")
		);

	}
}
