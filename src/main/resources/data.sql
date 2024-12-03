
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

-- add users
INSERT INTO users (id, email, login, name, password, birthday) VALUES
(1, 'alice@example.com', 'alice123', 'Alice Johnson', 'password1', '1990-05-10'),
(2, 'bob@example.com', 'bob456', 'Bob Smith', 'password2', '1985-08-22'),
(3, 'edward@example.com', 'edward202', 'Edward Cullen', 'password3', '1995-07-20');

-- add friendships
INSERT INTO user_friendships (user1_id, user2_id) VALUES
(1, 3),
(2, 3);

-- add genres mpa
INSERT INTO mpa_ratings (id, name) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

-- add films
INSERT INTO films (id, name, description, duration, release_date, mpa_id)
VALUES
(1, 'The Matrix', 'A computer hacker learns the truth about his reality.', 136, '1999-03-31', 1),
(2, 'Inception', 'A thief steals secrets through the use of dream-sharing technology.', 148, '2010-07-16', 2),
(3, 'The Dark Knight', 'Batman faces the Joker, a criminal mastermind.', 152, '2008-07-18', 3),
(4, 'Toy Story', 'Toys come to life and go on adventures.', 81, '1995-11-22', 4),
(5, 'Get Out', 'A man uncovers a disturbing secret while visiting his girlfriend’s family.', 104, '2017-02-24', 5);

-- add directors
INSERT INTO directors (id, name)
VALUES
(1, 'Spielberg'),
(2, 'Cameron');

-- add genres
INSERT INTO genres (id, name)
VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

-- add film genres
INSERT INTO film_genres (film_id, genre_id)
VALUES
(1, 6),
(2, 6),
(3, 6),
(4, 3),
(5, 4);


-- add film directors
INSERT INTO film_directors (film_id, director_id)
VALUES
(1, 2),
(2, 1);

INSERT INTO film_mpa (film_id, mpa_id)
VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);

INSERT INTO film_likes (film_id, user_id)
VALUES
(1, 1),
(2, 2),
(2, 3);
