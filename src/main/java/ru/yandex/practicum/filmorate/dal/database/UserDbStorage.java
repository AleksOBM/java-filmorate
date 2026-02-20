package ru.yandex.practicum.filmorate.dal.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.UserStorage;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.yandex.practicum.filmorate.dal.database.sql.UserQueryes.*;

@Repository
@Qualifier("userDbStorage")
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

	@Autowired
	public UserDbStorage(
			JdbcTemplate jdbc,
			@Qualifier("userRowMapper") RowMapper<User> mapper
	) {
		super(jdbc, mapper);
	}

	@Override
	public User createUser(User user) {
		long id = insertWithKeyHolder(
				SQL_USERS_INSERT,
				user.getName(),
				user.getLogin(),
				user.getEmail(),
				user.getBirthday()
		);
		user.setId(id);
		return user;
	}

	@Override
	public User updateUser(User user) {
		updateWithControl(
				SQL_USERS_UPDATE,
				user.getName(),
				user.getLogin(),
				user.getEmail(),
				user.getBirthday(),
				user.getId()
		);
		return user;
	}

	@Override
	@Transactional
	public Optional<User> findById(long userId) {
		Optional<User> userOptional = findOneByIdInTable(userId, "users");
		userOptional.ifPresent(user -> user.setFriendsIds(getFriendIds(userId)));
		return userOptional;
	}

	@Override
	public Collection<User> findAll() {
		return findManyUsers(SQL_USERS_FIND_ALL_USERS_AND_FRIENDS);
	}

	@Override
	public void addFriend(long userId, long friendId) {
		updateWithControl(
				SQL_USERS_ADD_FRIENDS,
				userId,
				friendId
		);
	}

	@Override
	public void removeFriend(long userId, long friendId) {
		updateWithControl(SQL_USERS_DELETE_FRIENDS, userId, friendId);
	}

	@Override
	public Collection<User> findFriendsOfUser(long userId) {
		return findManyUsers(SQL_USERS_FIND_ALL_FRIENDS_BY_USER_ID, userId);
	}

	@Override
	public Collection<User> findMutualFriends(long userId, long friendId) {
		return findManyUsers(SQL_USERS_FIND_MUTUAL_FRIENDS, userId, friendId);
	}

	@Override
	public Set<Long> getFriendIds(long userId) {
		String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
		return findColumnByQuery(sql, Long.class, userId);
	}

	@Override
	public void removeUser(long userId) {
		updateWithControl("DELETE FROM users WHERE id = ?", userId);
	}

	@Override
	public boolean checkUserIsNotPresent(Long userId) {
		return checkIdIsNotPresentInTable(userId, "users");
	}

	private Collection<User> findManyUsers(String sql, Object... params) {
		return jdbc.query(sql, rs -> {
			Map<Long, User> users = new LinkedHashMap<>();
			AtomicInteger rowNum = new AtomicInteger();
			while (rs.next()) {
				long userId = rs.getLong("id");
				User user = users.computeIfAbsent(userId, id -> {

					try {
						return rowMapper.mapRow(rs, rowNum.getAndIncrement());
					} catch (SQLException e) {
						throw new InternalServerException(
								"Не удалось получить пользователей из базы.\n" + e.getMessage()
						);
					}
				});

				long friendId = rs.getLong("friend_id");
				if (!rs.wasNull() && user != null) {
					user.addFriendId(friendId);
				}
			}
			return new ArrayList<>(users.values());
		}, params);
	}

}
