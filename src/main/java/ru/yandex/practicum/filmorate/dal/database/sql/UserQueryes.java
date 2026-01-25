package ru.yandex.practicum.filmorate.dal.database.sql;

public class UserQueryes {

	public static final String SQL_USERS_INSERT = """
			INSERT    INTO users (user_name, login, email, birthday)
			VALUES    (?, ?, ?, ?)
			""";

	public static final String SQL_USERS_UPDATE = """
			UPDATE    users
			SET       user_name = ?,
			          login = ?,
			          email = ?,
			          birthday = ?
			WHERE     id = ?
			""";

	public static final String SQL_USERS_ADD_FRIENDS = """
			INSERT    INTO friends (user_id, friend_id)
			VALUES    (?, ?)
			""";

	public static final String SQL_USERS_DELETE_FRIENDS = """
			DELETE    FROM friends
			WHERE     user_id = ?
			AND       friend_id = ?
			""";

	public static final String SQL_USERS_FIND_ALL_USERS_AND_FRIENDS = """
			SELECT    u.*,
			          f.friend_id
			FROM      users u
			LEFT JOIN friends f ON u.id = f.user_id
			ORDER BY  u.id;
			""";

	public static final String SQL_USERS_FIND_ALL_FRIENDS_BY_USER_ID = """
			SELECT    u.*,
			          f.FRIEND_ID
			FROM      USERS u
			LEFT JOIN FRIENDS f ON u.ID = f.USER_ID
			WHERE     u.ID IN (
			          SELECT    fr.friend_id
			          FROM      friends fr
			          WHERE     fr.user_id = ?
			          )
			ORDER BY  u.id
			""";

	public static final String SQL_USERS_FIND_MUTUAL_FRIENDS = """
			SELECT    u.*,
			          f.FRIEND_ID
			FROM      USERS u
			LEFT JOIN FRIENDS f ON u.ID = f.USER_ID
			WHERE     u.ID IN (
			          SELECT    fr.FRIEND_ID
			          FROM      friends fr
			          WHERE     fr.user_id IN (?)
			          AND       fr.FRIEND_ID IN (
			                    SELECT    fr1.FRIEND_ID
			                    FROM      friends fr1
			                    WHERE     fr1.user_id IN (?)
			                    )
			          )
			ORDER BY  u.id;
			""";
}
