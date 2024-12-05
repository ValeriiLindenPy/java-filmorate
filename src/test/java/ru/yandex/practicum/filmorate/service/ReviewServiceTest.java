package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.dto.RequestUpdateReviewDto;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ReviewService.class, ReviewMapper.class, ReviewStorage.class, UserDbStorage.class, FilmDbStorage.class, UserRowMapper.class, FilmRowMapper.class})
@AutoConfigureTestDatabase
class ReviewServiceTest {
    private final ReviewService reviewService;

    @Test
    void shouldCreatePositiveReview() {
        Review review = new Review(null, "Great movie!", true, 1L, 1L, 0L);
        Review created = reviewService.createReview(review);

        assertNotNull(created.getReviewId());
        assertEquals(true, created.getIsPositive());
        assertEquals(review.getContent(), created.getContent());
        assertEquals(0L, created.getUseful());
    }


    @Test
    void shouldUpdateReviewWithNewPositiveFlag() {
        Review review = new Review(null, "Good movie", true, 1L, 1L, 0L);
        Review created = reviewService.createReview(review);

        RequestUpdateReviewDto updateDto = new RequestUpdateReviewDto(created.getReviewId(), "Updated content", false, null, null);
        Review updated = reviewService.updateReview(updateDto);

        assertEquals("Updated content", updated.getContent());
        assertEquals(false, updated.getIsPositive());
    }

    @Test
    void shouldCreateReview() {
        Review review = new Review(null, "Great movie!", true, 1L, 2L, 0L);
        Review created = reviewService.createReview(review);

        assertNotNull(created.getReviewId());
        assertEquals(review.getContent(), created.getContent());
        assertEquals(0L, created.getUseful());
    }

    @Test
    void shouldUpdateReview() {
        Review review = new Review(null, "Good movie", true, 1L, 2L, 0L);
        Review created = reviewService.createReview(review);

        RequestUpdateReviewDto updateDto = new RequestUpdateReviewDto(created.getReviewId(), "Updated content", null, null, null);
        Review updated = reviewService.updateReview(updateDto);

        assertEquals("Updated content", updated.getContent());
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingNonexistentReview() {
        RequestUpdateReviewDto updateDto = new RequestUpdateReviewDto(999L, "Content", null, null, null);

        assertThrows(NotFoundException.class, () -> reviewService.updateReview(updateDto));
    }

    @Test
    void shouldDeleteReview() {
        Review review = new Review(null, "To delete", true, 1L, 2L, 0L);
        Review created = reviewService.createReview(review);

        reviewService.deleteById(created.getReviewId());
        assertThrows(NotFoundException.class, () -> reviewService.findById(created.getReviewId()));
    }

    @Test
    void shouldFindById() {
        Review review = new Review(null, "Find me", true, 1L, 2L, 0L);
        Review created = reviewService.createReview(review);

        Review found = reviewService.findById(created.getReviewId());
        assertEquals(created.getContent(), found.getContent());
    }

    @Test
    void shouldReturnAllReviews() {
        reviewService.createReview(new Review(null, "Review 1", true, 1L, 2L, 0L));
        reviewService.createReview(new Review(null, "Review 2", true, 1L, 2L, 0L));

        List<Review> reviews = reviewService.findTop(null, 10);
        assertEquals(4, reviews.size());
    }

    @Test
    void shouldAddLikeToReview() {
        Review review = reviewService.createReview(new Review(null, "Liked review", true, 1L, 2L, 0L));

        reviewService.addLike(review.getReviewId(), 1L);

        Review updated = reviewService.findById(review.getReviewId());
        assertTrue(updated.getUseful() > 0);
    }

    @Test
    void shouldRemoveLike() {
        Review review = reviewService.createReview(new Review(null, "Like to remove", true, 1L, 2L, 0L));
        reviewService.addLike(review.getReviewId(), 1L);

        reviewService.deleteLike(review.getReviewId(), 1L);

        Review updated = reviewService.findById(review.getReviewId());
        assertEquals(0L, updated.getUseful());
    }

    @Test
    void shouldHandleDislike() {
        Review review = reviewService.createReview(new Review(null, "Dislike this", true, 1L, 2L, 0L));
        reviewService.addDislike(review.getReviewId(), 1L);

        Review updated = reviewService.findById(review.getReviewId());
        assertTrue(updated.getUseful() < 0);
    }

    @Test
    void shouldRemoveDislike() {
        Review review = reviewService.createReview(new Review(null, "Remove dislike", true, 1L, 2L, 0L));
        reviewService.addDislike(review.getReviewId(), 1L);

        reviewService.deleteDislike(review.getReviewId(), 1L);

        Review updated = reviewService.findById(review.getReviewId());
        assertEquals(0L, updated.getUseful());
    }
}