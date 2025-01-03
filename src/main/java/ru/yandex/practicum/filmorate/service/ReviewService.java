package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.dto.RequestUpdateReviewDto;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    /**
     * Create a review
     *
     * @param review
     * @return {@link Review}
     */
    public Review createReview(Review review) {
        userStorage.getById(review.getUserId())
                .orElseThrow(() -> new NotFoundException(
                        "User with id = %d not found".formatted(review.getUserId()))
                );

        filmStorage.getById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException(
                        "Film with id = %d not found".formatted(review.getFilmId())
                ));

        Long id = reviewStorage.createReview(review);

        // update fields in Review
        review.setReviewId(id);
        review.setUseful(0L);
        addNewEvent(review.getUserId(), review.getReviewId(), EventType.REVIEW, OperationType.ADD);
        return review;
    }

    /**
     * Updates a review
     *
     * @param reviewDto
     * @return {@link Review}
     */
    public Review updateReview(RequestUpdateReviewDto reviewDto) {
        Review review = reviewStorage.findById(reviewDto.getReviewId())
                .orElseThrow(() -> new NotFoundException(
                        "Review with id = %d not found".formatted(reviewDto.getReviewId())
                ));

        if (reviewDto.getUserId() != null && userStorage.getById(reviewDto.getUserId()).isEmpty()) {
            throw new NotFoundException("User with id = %d not found".formatted(reviewDto.getUserId()));
        }

        if (reviewDto.getFilmId() != null && filmStorage.getById(reviewDto.getFilmId()).isEmpty()) {
            throw new NotFoundException("Film with id = %d not found".formatted(reviewDto.getFilmId()));
        }

        if (reviewDto.getContent() != null) {
            if (reviewDto.getContent().isBlank()) {
                throw new ValidationException("Content cannot be blank");
            }

            review.setContent(reviewDto.getContent());
        }

        if (reviewDto.getIsPositive() != null) {
            review.setIsPositive(reviewDto.getIsPositive());
        }
        review = reviewStorage.updateReview(review);

        addNewEvent(review.getUserId(), review.getReviewId(), EventType.REVIEW, OperationType.UPDATE);

        return review;
    }

    /**
     * Deletes review
     *
     * @param id
     */
    public void deleteById(Long id) {
        addNewEvent(reviewStorage.findById(id).get().getUserId(), id, EventType.REVIEW, OperationType.REMOVE);
        reviewStorage.deleteById(id);
    }

    /**
     * Retrieves a review by id
     *
     * @param id
     * @return {@link Review}
     */
    public Review findById(Long id) {
        return reviewStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Review with id = %d not found".formatted(id)
                ));
    }


    /**
     * Get review top
     *
     * @param filmDd
     * @param limit
     * @return {@link List<Review>}
     */
    public List<Review> findTop(Long filmDd, long limit) {
        if (filmDd == null) {
            return reviewStorage.findTop(limit);
        } else {
            return reviewStorage.findTopByFilmId(filmDd, limit);
        }
    }

    /**
     * Add like or dislike depending on {@link  Boolean} isLike
     *
     * @param reviewId
     * @param userId
     * @param isLike
     */
    public void addLike(Long reviewId, Long userId, boolean isLike) {
        reviewStorage.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(
                        "Review with id = %d not found".formatted(reviewId)
                ));

        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException(
                        "User with id = %d not found".formatted(userId)
                ));

        reviewStorage.addLike(reviewId, userId, isLike);
    }

    /**
     * Deletes like or dislike depending on {@link  Boolean} isLike
     *
     * @param reviewId
     * @param userId
     */
    public void deleteLike(Long reviewId, Long userId) {
        if (reviewStorage.findById(reviewId).isEmpty()) {
            throw new NotFoundException("Review with id = %d not found".formatted(reviewId));
        }

        if (userStorage.getById(userId).isEmpty()) {
            throw new NotFoundException("User with id = %d not found".formatted(userId));
        }

        reviewStorage.deleteLike(reviewId, userId);
    }

    private void addNewEvent(Long userId, Long reviewId, EventType eventType, OperationType operationType) {
        Event event = Event.builder()
                .userId(userId)
                .eventType(eventType)
                .operation(operationType)
                .timestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000)
                .entityId(reviewId)
                .build();
        eventStorage.addEvent(event);
    }
}
