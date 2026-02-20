package ru.yandex.practicum.filmorate.dal.database.sql;

public class FilmQueryes {

	public static final String SQL_FILMS_FIND_ALL = """
			SELECT    mrg.id,
			          mrg.FILM_NAME,
			          mrg.DESCRIPTION,
			          mrg.RELEASE_DATE,
			          mrg.DURATION,
			          mrg.MPA_ID,
			          gof.GENRE_ID,
					  dof.DIRECTOR_ID,
			          user_id
			FROM      (
			          SELECT    f.ID,
			                    f.FILM_NAME,
			                    DESCRIPTION,
			                    RELEASE_DATE,
			                    DURATION,
			                    MPA_ID,
			                    l.USER_ID
			          FROM      FILMS f
			          LEFT JOIN LIKES l ON f.ID = l.FILM_ID
			          ORDER BY  f.ID
			          ) AS mrg
			LEFT JOIN GENRES_OF_FILMS gof ON mrg.id = gof.FILM_ID
			LEFT JOIN DIRECTORS_OF_FILMS dof ON mrg.ID = dof.FILM_ID
			""";

	public static final String SQL_FILMS_FIND_ALL_OF_DIRECTOR = """
			SELECT    mrg.id,
			          mrg.FILM_NAME,
			          mrg.DESCRIPTION,
			          mrg.RELEASE_DATE,
			          mrg.DURATION,
			          mrg.MPA_ID,
			          gof.GENRE_ID,
			          dof.DIRECTOR_ID,
			          user_id
			FROM      (
			          SELECT    f.ID,
			                    f.FILM_NAME,
			                    DESCRIPTION,
			                    RELEASE_DATE,
			                    DURATION,
			                    MPA_ID,
			                    l.USER_ID
			          FROM      FILMS f
			          LEFT JOIN LIKES l ON f.ID = l.FILM_ID
			          ORDER BY  f.ID
			          ) AS mrg
			LEFT JOIN GENRES_OF_FILMS gof ON mrg.id = gof.FILM_ID
			LEFT JOIN DIRECTORS_OF_FILMS dof ON mrg.ID = dof.FILM_ID
			WHERE     mrg.ID IN (
			          SELECT    f1.ID
			          FROM      FILMS f1
			          WHERE     f1.ID IN (
			                    SELECT    dof2.FILM_ID
			                    FROM      DIRECTORS_OF_FILMS dof2
			                    WHERE     dof2.DIRECTOR_ID = ?
			                    )
			          )
			""";

	public static final String SQL_FILMS_FIND_TOP = """
			SELECT    mrg.id,
			          mrg.FILM_NAME,
			          mrg.DESCRIPTION,
			          mrg.RELEASE_DATE,
			          mrg.DURATION,
			          mrg.MPA_ID,
			          gof.GENRE_ID,
					  dof.DIRECTOR_ID,
			          user_id
			FROM      (
			          SELECT    f.ID,
			                    f.FILM_NAME,
			                    DESCRIPTION,
			                    RELEASE_DATE,
			                    DURATION,
			                    MPA_ID,
			                    l.USER_ID
			          FROM      FILMS f
			          LEFT JOIN LIKES l ON f.ID = l.FILM_ID
			          ) AS mrg
			LEFT JOIN GENRES_OF_FILMS gof ON mrg.id = gof.FILM_ID
			LEFT JOIN DIRECTORS_OF_FILMS dof ON mrg.ID = dof.FILM_ID
			WHERE     mrg.id IN (
			          SELECT    rte.ID
			          FROM      (
			                    SELECT    flm.*,
			                              COUNT(lks.ID) AS rate
			                    FROM      FILMS flm
			                    LEFT JOIN LIKES lks ON flm.ID = lks.FILM_ID
			                    GROUP BY  flm.ID
			                    ) AS rte
			          ORDER BY  rate DESC, rte.ID
			          LIMIT     ?
			          )
			""";

	public static final String SQL_FILMS_SET_LIKE = """
			INSERT    INTO LIKES (FILM_ID, USER_ID)
			VALUES    (?, ?)
			""";

	public static final String SQL_FILMS_DELETE_LIKE = """
			DELETE    FROM LIKES
			WHERE     film_id = ?
			AND       user_id = ?;
			""";

	public static final String SQL_FILMS_FIND_GENREIDS_BY_FILM_ID = """
			SELECT    g.ID
			FROM      GENRES g
			JOIN      GENRES_OF_FILMS gof ON g.ID = gof.GENRE_ID
			WHERE     gof.FILM_ID = ?
			""";

	public static final String SQL_FILMS_INSERT_GENREIDS = """
			MERGE INTO GENRES_OF_FILMS (FILM_ID, GENRE_ID) KEY (FILM_ID, GENRE_ID) VALUES
			""";

	public static final String SQL_FILMS_FIND_DIRECTORIDS_BY_FILM_ID = """
			SELECT    d.ID
			FROM      DIRECTORS d
			JOIN      DIRECTORS_OF_FILMS dof ON d.ID = dof.DIRECTOR_ID
			WHERE     dof.FILM_ID = ?
			""";

	public static final String SQL_FILMS_INSERT_DIRECTORIDS = """
			MERGE INTO DIRECTORS_OF_FILMS (FILM_ID, DIRECTOR_ID) KEY (FILM_ID, DIRECTOR_ID) VALUES
			""";

	public static final String SQL_FILMS_INSERT = """
			INSERT INTO films (
			       film_name,
			       description,
			       release_date,
			       duration,
			       mpa_id
			       )
			VALUES (?, ?, ?, ?, ?)
			""";

	public static final String SQL_FILMS_UPDATE = """
			UPDATE films
			   SET film_name = ?,
			       description = ?,
			       release_date = ?,
			       duration = ?,
			       mpa_id = ?
			 WHERE id = ?
			""";

	public static final String SQL_FILMS_FIND_TOP_BY_GENRES = """
			SELECT f.*, COUNT(fl.user_id) as likes_count
			FROM films f
			LEFT JOIN likes fl ON f.id = fl.film_id
			LEFT JOIN genres_of_films gr ON f.id = gr.film_id
			WHERE gr.genre_id = ?
			GROUP BY f.id
			ORDER BY likes_count DESC
			LIMIT ?
			""";

	public static final String SQL_FILMS_FIND_TOP_BY_YEAR = """
			SELECT f.*, COUNT(fl.user_id) as likes_count
			FROM (SELECT * FROM films WHERE EXTRACT(YEAR FROM release_date) = ?) f
			LEFT JOIN likes fl ON f.id = fl.film_id
			GROUP BY f.id
			ORDER BY likes_count DESC
			LIMIT ?
			""";

	public static final String SQL_FILMS_FIND_TOP_BY_GENRE_AND_YEAR = """
			SELECT f.*, COUNT(fl.user_id) as likes_count
			FROM (SELECT * FROM films WHERE EXTRACT(YEAR FROM release_date) = ?) f
			LEFT JOIN likes fl ON f.id = fl.film_id
			LEFT JOIN genres_of_films gr ON f.id = gr.film_id
			WHERE gr.genre_id = ?
			GROUP BY f.id
			ORDER BY likes_count DESC
			LIMIT ?
			""";

	public static final String SQL_FILMS_FIND_COMMON_LIKED = """
           SELECT f.id,
           f.film_name,
           f.description,
           f.release_date,
           f.duration,
           f.mpa_id,
           l_all.user_id,
           g.genre_id,
           d.director_id,
           (SELECT COUNT(*) FROM likes WHERE film_id = f.id) as likes_count
    FROM films f
    LEFT JOIN likes l_all ON f.id = l_all.film_id
    LEFT JOIN genres_of_films g ON f.id = g.film_id
    LEFT JOIN directors_of_films d ON f.id = d.film_id
    WHERE f.id IN (
        SELECT l1.film_id
        FROM likes l1
        JOIN likes l2 ON l1.film_id = l2.film_id
        WHERE l1.user_id = ? AND l2.user_id = ?
    )
    ORDER BY likes_count DESC;
    """;
}
