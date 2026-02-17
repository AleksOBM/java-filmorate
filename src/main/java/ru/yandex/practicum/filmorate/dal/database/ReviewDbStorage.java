package ru.yandex.practicum.filmorate.dal.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.ReviewStorage;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.dal.database.sql.ReviewQueries.*;

@Repository
@Qualifier("reviewDbStorage")
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbc, @Qualifier("reviewRowMapper") RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    @Transactional
    public Review createReview(Review review) {
        long id = insertWithKeyHolder(
                SQL_REVIEWS_INSERT,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId()
        );
        review.setId(id);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        updateWithControl(
                SQL_REVIEWS_UPDATE,
                review.getContent(),
                review.getIsPositive(),
                review.getId()
        );
        return findReviewById(review.getId()).get();
    }

    @Override
    public void deleteReview(long id) {
        updateWithControl(SQL_REVIEWS_DELETE, id);
    }

    @Override
    public Optional<Review> findReviewById(long reviewId) {
        Optional<Review> review = findOneByIdInTable(reviewId, "reviews");
        if (review.isPresent()) {
            review.get().setUserLikes(findAllReactionsByReviewId(reviewId));
            return review;
        }
        return Optional.empty();
    }

    @Override
    public Collection<Review> findReviewsByFilmId(Long filmId, int count) {
        Collection<Review> reviews;
        if (filmId == null) {
            reviews = findManyByQuery(SQL_REVIEWS_FIND_ALL, count);
        } else {
            reviews = findManyByQuery(SQL_REVIEWS_FIND_BY_FILM_ID, filmId, count);
        }
        reviews.forEach(review ->{
            review.setUserLikes(findAllReactionsByReviewId(review.getId()));
        });
        return reviews;
    }

    @Override
    @Transactional
    public void addReviewLike(long reviewId, long userId) {
        updateWithControl(SQL_REVIEWS_ADD_LIKE, reviewId, userId);
        updateWithControl(SQL_REVIEWS_CALCULATE_USEFUL, reviewId, reviewId);
    }

    @Override
    @Transactional
    public void addReviewDislike(long reviewId, long userId) {
        updateWithControl(SQL_REVIEWS_ADD_DISLIKE, reviewId, userId);
        updateWithControl(SQL_REVIEWS_CALCULATE_USEFUL, reviewId, reviewId);
    }

    @Override
    @Transactional
    public void removeReviewLike(long reviewId, long userId) {
        updateWithControl(SQL_REVIEWS_DELETE_LIKE, reviewId, userId);
        updateWithControl(SQL_REVIEWS_CALCULATE_USEFUL, reviewId, reviewId);
    }

    @Override
    @Transactional
    public void removeReviewDislike(long reviewId, long userId) {
        updateWithControl(SQL_REVIEWS_DELETE_DISLIKE, reviewId, userId);
        updateWithControl(SQL_REVIEWS_CALCULATE_USEFUL, reviewId, reviewId);
    }

    @Override
    public boolean checkReviewIsNotPresent(long reviewId){
        return checkIdIsNotPresentInTable(reviewId, "reviews");
    }

    public Optional<Boolean> getReactionsStatus(long reviewId, long userId) {
        return jdbc.query(SQL_REVIEWS_LIKE_STATUS, (rs, rowNum) -> rs.getBoolean("is_like"), reviewId, userId)
                .stream()
                .findFirst();
    }

    private Map<Long, Boolean> findAllReactionsByReviewId(long reviewId) {
        return jdbc.query(SQL_REVIEWS_GET_LIKES_BY_REVIEW_ID, rs -> {
            Map<Long, Boolean> reactions = new HashMap<>();
            while (rs.next()) {
                reactions.put(
                        rs.getLong("user_id"),
                        rs.getBoolean("is_like")
                );
            }
            return reactions;
        }, reviewId);
    }
}
