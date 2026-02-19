package ru.yandex.practicum.filmorate.servise;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.dal.ReviewStorage;
import ru.yandex.practicum.filmorate.dal.UserStorage;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.request.create.ReviewCreateRequest;
import ru.yandex.practicum.filmorate.dto.request.update.ReviewUpdateRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventService eventService;

    @Autowired
    public ReviewService(
            @Qualifier("reviewDbStorage") ReviewStorage reviewStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            EventService eventService
    ) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventService = eventService;
    }

    public ReviewDto createReview(ReviewCreateRequest request) {
        log.info("Добавление нового отзыва пользователем id={} для фильма id={}", request.getUserId(), request.getFilmId());

        checkUserExists(request.getUserId());
        checkFilmExists(request.getFilmId());

        Review review = ReviewMapper.mapToReview(request);
        review = reviewStorage.createReview(review);
        eventService.addUserEvent(review.getUserId(), EventType.REVIEW, Operation.ADD, review.getId());

        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto updateReview(ReviewUpdateRequest request) {
        log.info("Обновление данных об отзыве с id={}", request.getReviewId());

        Review oldReview = reviewStorage.findReviewById(request.getReviewId())
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + request.getReviewId() + " не найден."));

        Review newReview = ReviewMapper.updateReviewFields(oldReview, request);
        newReview = reviewStorage.updateReview(newReview);
        eventService.addUserEvent(newReview.getUserId(), EventType.REVIEW, Operation.UPDATE, newReview.getId());

        return ReviewMapper.mapToReviewDto(newReview);
    }

    public void deleteReview(Long id) {
        log.info("Удаление отзыва с id={}", id);
        checkReviewExists(id);
        long userId = findReviewById(id).getUserId();
        reviewStorage.deleteReview(id);
        eventService.addUserEvent(userId, EventType.REVIEW, Operation.REMOVE, id);
    }

    public ReviewDto findReviewById(Long id) {
        log.info("Получение отзыва с id={}", id);
        return reviewStorage.findReviewById(id)
                .map(ReviewMapper::mapToReviewDto)
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + id + " не найден."));
    }

    public Collection<ReviewDto> findReviewsByFilmId(Long filmId, int count) {
        log.info("Получение отзывов для фильма с id={} в количестве {}", filmId, count);
        return reviewStorage.findReviewsByFilmId(filmId, count).stream()
                .map(ReviewMapper::mapToReviewDto)
                .toList();
    }

    public void addReviewLike(Long id, Long userId) {
        log.info("Пользователь id={} ставит лайк отзыву id={}", userId, id);
        checkReviewExists(id);
        checkUserExists(userId);

        Optional<Boolean> likeStatus = reviewStorage.getReactionsStatus(id, userId);

        if (likeStatus.isEmpty()) {
            reviewStorage.addReviewLike(id, userId);
        } else if (!likeStatus.get()) {
            reviewStorage.removeReviewDislike(id, userId);
            reviewStorage.addReviewLike(id, userId);
        }

    }

    public void addReviewDislike(Long id, Long userId) {
        log.info("Пользователь id={} ставит дизлайк отзыву id={}", userId, id);
        checkReviewExists(id);
        checkUserExists(userId);
        Optional<Boolean> likeStatus = reviewStorage.getReactionsStatus(id, userId);

        if (likeStatus.isEmpty()) {
            reviewStorage.addReviewDislike(id, userId);
        } else if (likeStatus.get()) {
            reviewStorage.removeReviewLike(id, userId);
            reviewStorage.addReviewDislike(id, userId);
        }

    }

    public void removeReviewLike(Long id, Long userId) {
        log.info("Пользователь id={} удаляет лайк у отзыва id={}", userId, id);
        checkReviewExists(id);
        checkUserExists(userId);
        reviewStorage.removeReviewLike(id, userId);
    }

    public void removeReviewDislike(Long id, Long userId) {
        log.info("Пользователь id={} удаляет дизлайк у отзыва id={}", userId, id);
        checkReviewExists(id);
        checkUserExists(userId);
        reviewStorage.removeReviewDislike(id, userId);
    }

    private void checkFilmExists(Long filmId) {
        if (filmStorage.checkFilmIsNotPresent(filmId)) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
        }
    }

    private void checkUserExists(Long userId) {
        if (userStorage.checkUserIsNotPresent(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
    }

    private void checkReviewExists(Long id) {
        if (reviewStorage.checkReviewIsNotPresent(id)) {
            throw new NotFoundException("Отзыв с id=" + id + " не найден.");
        }
    }
}
