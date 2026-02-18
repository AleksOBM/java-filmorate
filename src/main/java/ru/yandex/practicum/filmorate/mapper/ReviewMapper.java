package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.request.create.ReviewCreateRequest;
import ru.yandex.practicum.filmorate.dto.request.update.ReviewUpdateRequest;
import ru.yandex.practicum.filmorate.model.Review;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewMapper {

    public static Review mapToReview(ReviewCreateRequest request) {
        return Review.builder()
                .content(request.getContent())
                .isPositive(request.getIsPositive())
                .userId(request.getUserId())
                .filmId(request.getFilmId())
                .build();
    }

    public static Review updateReviewFields(Review review, ReviewUpdateRequest request) {
        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }
        if (request.getIsPositive() != null) {
            review.setIsPositive(request.getIsPositive());
        }
        return review;
    }

    public static ReviewDto mapToReviewDto(Review review) {
        return ReviewDto.builder()
                .reviewId(review.getId())
                .content(review.getContent())
                .isPositive(review.getIsPositive())
                .userId(review.getUserId())
                .filmId(review.getFilmId())
                .useful(review.getUseful())
                .build();
    }
}
