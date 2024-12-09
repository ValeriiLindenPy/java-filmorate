--
-- Clear existing data
DELETE FROM film_mpa;
DELETE FROM film_genres;
DELETE FROM films;
DELETE FROM genres;
DELETE FROM directors;
DELETE FROM mpa_ratings;
DELETE FROM user_friendships;
DELETE FROM users;
DELETE FROM film_likes;
DELETE FROM reviews;
DELETE FROM review_ratings;

-- Add users
MERGE INTO users (id, email, login, name, password, birthday)
KEY(id)
VALUES
  (1, 'alice@example.com', 'alice123', 'Alice Johnson', 'password1', '1990-05-10'),
  (2, 'bob@example.com', 'bob456', 'Bob Smith', 'password2', '1985-08-22'),
  (3, 'edward@example.com', 'edward202', 'Edward Cullen', 'password3', '1995-07-20');

-- Add friendships
MERGE INTO user_friendships (user1_id, user2_id)
KEY(user1_id, user2_id)
VALUES
  (1, 3),
  (1, 2),
  (2, 3);

-- Add MPA ratings
MERGE INTO mpa_ratings (id, name)
KEY(id)
VALUES
  (1, 'G'),
  (2, 'PG'),
  (3, 'PG-13'),
  (4, 'R'),
  (5, 'NC-17');

-- Add films
MERGE INTO films (id, name, description, duration, release_date, mpa_id)
KEY(id)
VALUES
  (1, 'The Matrix', 'A computer hacker learns the truth about his reality.', 136, '1999-03-31', 1),
  (2, 'Inception', 'A thief steals secrets through the use of dream-sharing technology.', 148, '2010-07-16', 2),
  (3, 'The Dark Knight', 'Batman faces the Joker, a criminal mastermind.', 152, '2008-07-18', 3),
  (4, 'Toy Story', 'Toys come to life and go on adventures.', 81, '1995-11-22', 4),
  (5, 'Get Out', 'A man uncovers a disturbing secret while visiting his girlfriend’s family.', 104, '2017-02-24', 5);

-- Add directors
MERGE INTO directors (id, name)
KEY(id)
VALUES
  (1, 'Spielberg'),
  (2, 'Cameron');

-- Add genres
MERGE INTO genres (id, name)
KEY(id)
VALUES
  (1, 'Комедия'),
  (2, 'Драма'),
  (3, 'Мультфильм'),
  (4, 'Триллер'),
  (5, 'Документальный'),
  (6, 'Боевик');

-- Add film genres
MERGE INTO film_genres (film_id, genre_id)
KEY(film_id, genre_id)
VALUES
  (1, 6),
  (1, 3),
  (2, 6),
  (3, 6),
  (4, 3),
  (5, 4);

-- Add film directors
MERGE INTO film_directors (film_id, director_id)
KEY(film_id, director_id)
VALUES
  (1, 2),
  (1, 1),
  (2, 1);

-- Add film MPA ratings
MERGE INTO film_mpa (film_id, mpa_id)
KEY(film_id, mpa_id)
VALUES
  (1, 1),
  (2, 2),
  (3, 3),
  (4, 4),
  (5, 5);

-- Add film likes
MERGE INTO film_likes (film_id, user_id)
KEY(film_id, user_id)
VALUES
  (1, 1),
  (2, 1),
  (2, 2),
  (2, 3);

-- review example
INSERT INTO reviews (content, is_positive, user_id, film_id)
VALUES
('Amazing film!', true, 1, 1),
('Not worth watching.', false, 2, 1);

-- review like/dislike
INSERT INTO review_ratings (review_id, user_id, is_like)
VALUES
(1, 1, true),  -- like user 1
(1, 2, true),  -- like user 2
(2, 3, false); -- dislike user 1
