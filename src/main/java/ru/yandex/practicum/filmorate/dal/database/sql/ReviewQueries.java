package ru.yandex.practicum.filmorate.dal.database.sql;

public class ReviewQueries {

    public static final String SQL_REVIEWS_INSERT =
            "INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?)";

    public static final String SQL_REVIEWS_UPDATE =
            "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";

    public static final String SQL_REVIEWS_DELETE =
            "DELETE FROM reviews WHERE id = ?";

    public static final String SQL_REVIEWS_FIND_ALL =
            "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";

    public static final String SQL_REVIEWS_FIND_BY_FILM_ID =
            "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";

    public static final String SQL_REVIEWS_UPDATE_USEFUL =
            "UPDATE reviews SET useful = useful + ? WHERE id = ?";

    public static final String SQL_REVIEWS_ADD_LIKE =
            "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, TRUE)";

    public static final String SQL_REVIEWS_ADD_DISLIKE =
            "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, FALSE)";

    public static final String SQL_REVIEWS_DELETE_LIKE =
            "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = TRUE";

    public static final String SQL_REVIEWS_DELETE_DISLIKE =
            "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = FALSE";

    public static final String SQL_REVIEWS_LIKE_STATUS =
            "SELECT is_like FROM review_likes WHERE review_id = ? AND user_id = ?";

    public static final String SQL_REVIEWS_GET_LIKES_BY_REVIEW_ID =
            "SELECT user_id, is_like FROM review_likes WHERE review_id = ?";
}
