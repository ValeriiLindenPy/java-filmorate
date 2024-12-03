package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.dto.RequestUpdateReviewDto;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userService;
    private final FilmStorage filmService;

    public Review createReview(Review review) {
        if (userService.getById(review.getUserId()).isEmpty()) {
            throw new NotFoundException("User with id = %d not found".formatted(review.getUserId()));
        }

        if (filmService.getById(review.getFilmId()).isEmpty()) {
            throw new NotFoundException("Film with id = %d not found".formatted(review.getFilmId()));
        }

        Long id = reviewStorage.createReview(review);

        // update fields in Review
        review.setReviewId(id);
        review.setUseful(0L);

        return review;
    }

    public Review updateReview(RequestUpdateReviewDto reviewDto) {
        Review review = reviewStorage.findById(reviewDto.getReviewId())
                .orElseThrow(() -> new NotFoundException(
                        "Review with id = %d not found".formatted(reviewDto.getReviewId())
                ));

        if (reviewDto.getUserId() != null && userService.getById(reviewDto.getUserId()).isEmpty()) {
            throw new NotFoundException("User with id = %d not found".formatted(reviewDto.getUserId()));
        }

        if (reviewDto.getFilmId() != null && filmService.getById(reviewDto.getFilmId()).isEmpty()) {
            throw new NotFoundException("Film with id = %d not found".formatted(reviewDto.getFilmId()));
        }

        if (reviewDto.getContent() != null) {
            review.setContent(reviewDto.getContent());
        }

        if (reviewDto.getUserId() != null) {
            review.setUserId(reviewDto.getUserId());
        }

        if (reviewDto.getFilmId() != null) {
            review.setFilmId(reviewDto.getFilmId());
        }

        if (reviewDto.getIsPositive() != null) {
            review.setIsPositive(reviewDto.getIsPositive());
        }

        return reviewStorage.updateReview(review);
    }

    public void deleteById(Long id) {
        reviewStorage.deleteById(id);
    }

    public Review findById(Long id) {
        return reviewStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Review with id = %d not found".formatted(id)
                ));
    }

    public List<Review> findTop(Long filmDd, long limit) {
        if (filmDd == null) {
            return reviewStorage.findTop(limit);
        } else {
            return reviewStorage.findTopByFilmId(filmDd, limit);
        }
    }

    public void addLike(Long reviewId, Long userId) {
        addLikeDislike(reviewId, userId, true);
    }

    public void addDislike(Long reviewId, Long userId) {
        addLikeDislike(reviewId, userId, false);
    }

    private void addLikeDislike(Long reviewId, Long userId, boolean isLike) {
        if (reviewStorage.findById(reviewId).isEmpty()) {
            throw new NotFoundException("Review with id = %d not found".formatted(reviewId));
        }

        if (userService.getById(userId).isEmpty()) {
            throw new NotFoundException("User with id = %d not found".formatted(userId));
        }

        if (isLike) {
            reviewStorage.addLike(reviewId, userId);
        } else {
            reviewStorage.addDislike(reviewId, userId);
        }
    }

    public void deleteLike(Long reviewId, Long userId) {
        deleteLikeDislike(reviewId, userId, true);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        deleteLikeDislike(reviewId, userId, false);
    }

    private void deleteLikeDislike(Long reviewId, Long userId, boolean isLike) {
        if (reviewStorage.findById(reviewId).isEmpty()) {
            throw new NotFoundException("Review with id = %d not found".formatted(reviewId));
        }

        if (userService.getById(userId).isEmpty()) {
            throw new NotFoundException("User with id = %d not found".formatted(userId));
        }

        if (isLike) {
            reviewStorage.deleteLike(reviewId, userId);
        } else {
            reviewStorage.deleteDislike(reviewId, userId);
        }
    }
}
