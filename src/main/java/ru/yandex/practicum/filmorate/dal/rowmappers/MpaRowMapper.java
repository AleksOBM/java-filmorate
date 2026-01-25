package ru.yandex.practicum.filmorate.dal.rowmappers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Qualifier("mpaRowMapper")
public class MpaRowMapper implements RowMapper<Mpa> {

	@Override
	public Mpa mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		return new Mpa(
				resultSet.getInt("id"),
				resultSet.getString("mpa_name")
		);
	}
}
