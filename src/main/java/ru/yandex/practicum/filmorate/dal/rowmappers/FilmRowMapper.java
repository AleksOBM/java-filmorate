package ru.yandex.practicum.filmorate.dal.rowmappers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

@Component
@Qualifier("filmRowMapper")
public class FilmRowMapper implements RowMapper<Film> {
	@Override
	public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		return Film.builder()
				.id(resultSet.getLong("id"))
				.name(resultSet.getString("film_name"))
				.description(resultSet.getString("description"))
				.releaseDate(resultSet.getDate("release_date").toLocalDate())
				.duration(Duration.ofMinutes(resultSet.getInt("duration")))
				.mpaId(resultSet.getInt("mpa_id"))
				.averageAssessment(resultSet.getFloat("assessment"))
				.build();
	}

}
