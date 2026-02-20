package ru.yandex.practicum.filmorate.dal.rowmappers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Qualifier("directorRowMapper")
public class DirectorRowMapper implements RowMapper<Director> {

	@Override
	public Director mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		return Director.builder()
				.id(resultSet.getInt("id"))
				.name(resultSet.getString("director_name"))
				.build();
	}
}
