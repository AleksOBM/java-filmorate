package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReview(long id);

    Optional<Review> findReviewById(long id);

    Collection<Review> findReviewsByFilmId(Long filmId, int count);

    void addReviewLike(long id, long userId);

    void addReviewDislike(long id, long userId);

    void removeReviewLike(long id, long userId);

    void removeReviewDislike(long id, long userId);

    boolean checkReviewIsNotPresent(long id);

    Optional<Boolean> getReactionsStatus(long reviewId, long userId);
}

