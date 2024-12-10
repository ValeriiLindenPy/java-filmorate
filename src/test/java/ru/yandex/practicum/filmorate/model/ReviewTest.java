package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldThrowValidationErrorWhenContentIsBlank() {
        Review invalidReview = new Review(null, "   ", true, 1L, 1L, 0L);

        Set<ConstraintViolation<Review>> violations = validator.validate(invalidReview);
        assertEquals(1, violations.size());
        assertEquals("Content cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void shouldThrowValidationErrorWhenIsPositiveIsNull() {
        Review invalidReview = new Review(null, "Content", null, 1L, 1L, 0L);

        Set<ConstraintViolation<Review>> violations = validator.validate(invalidReview);
        assertEquals(1, violations.size());
        assertEquals("isPositive cannot be null", violations.iterator().next().getMessage());
    }

}