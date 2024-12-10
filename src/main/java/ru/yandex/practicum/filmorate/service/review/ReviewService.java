package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.dto.RequestUpdateReviewDto;
import ru.yandex.practicum.filmorate.service.event.EventService;
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
    private final EventService eventService;

    /**
     * Create a review
     *
     * @param review
     * @return {@link Review}
     */
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
        eventService.createEvent(
                review.getUserId(),
                EventType.REVIEW,
                OperationType.ADD,
                review.getReviewId()
        );

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

        if (reviewDto.getUserId() != null && userService.getById(reviewDto.getUserId()).isEmpty()) {
            throw new NotFoundException("User with id = %d not found".formatted(reviewDto.getUserId()));
        }

        if (reviewDto.getFilmId() != null && filmService.getById(reviewDto.getFilmId()).isEmpty()) {
            throw new NotFoundException("Film with id = %d not found".formatted(reviewDto.getFilmId()));
        }

        if (reviewDto.getContent() != null) {
            if (reviewDto.getContent().isBlank()) {
                throw new ValidationException("Content cannot be blank");
            }

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
        review = reviewStorage.updateReview(review);

        eventService.createEvent(
                review.getUserId(),
                EventType.REVIEW,
                OperationType.UPDATE,
                review.getReviewId()
        );

        return review;
    }

    /**
     * Deletes review
     *
     * @param id
     */
    public void deleteById(Long id) {
        eventService.createEvent(
                reviewStorage.findById(id).get().getUserId(),
                EventType.REVIEW,
                OperationType.REMOVE,
                id
        );
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
     * Add like to a review
     *
     * @param reviewId
     * @param userId
     */
    public void addLike(Long reviewId, Long userId) {
        addLikeDislike(reviewId, userId, true);
        eventService.createEvent(
                userId,
                EventType.LIKE,
                OperationType.ADD,
                reviewId
        );
    }

    /**
     * Add dislike to a review
     *
     * @param reviewId
     * @param userId
     */
    public void addDislike(Long reviewId, Long userId) {
        addLikeDislike(reviewId, userId, false);
    }

    /**
     * Add like or dislike depending on {@link  Boolean} isLike
     * @param reviewId
     * @param userId
     * @param isLike
     */
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

    /**
     * deletes like to a review
     *
     * @param reviewId
     * @param userId
     */
    public void deleteLike(Long reviewId, Long userId) {
        deleteLikeDislike(reviewId, userId, true);
        eventService.createEvent(
                userId,
                EventType.LIKE,
                OperationType.REMOVE,
                reviewId
        );
    }

    /**
     * Deletes dislike to a review
     *
     * @param reviewId
     * @param userId
     */
    public void deleteDislike(Long reviewId, Long userId) {
        deleteLikeDislike(reviewId, userId, false);
    }

    /**
     * Deletes like or dislike depending on {@link  Boolean} isLike
     * @param reviewId
     * @param userId
     * @param isLike
     */
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
