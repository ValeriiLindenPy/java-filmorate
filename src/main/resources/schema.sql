-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    password VARCHAR(255),
    birthday DATE NOT NULL,
    CHECK (birthday <= CURRENT_DATE)
);

-- Friendship Table
CREATE TABLE IF NOT EXISTS user_friendships (
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    CONSTRAINT fk_user1 FOREIGN KEY (user1_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user2 FOREIGN KEY (user2_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_no_self_reference CHECK (user1_id <> user2_id),
    PRIMARY KEY (user1_id, user2_id)
);

-- Genres Table
CREATE TABLE IF NOT EXISTS genres (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- MPA ratings Table
CREATE TABLE IF NOT EXISTS mpa_ratings (
    id BIGINT PRIMARY KEY,
    name VARCHAR(10) NOT NULL
);

-- Films Table
CREATE TABLE IF NOT EXISTS films (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200) NOT NULL,
    duration INTEGER NOT NULL,
    release_date DATE NOT NULL,
    mpa_id BIGINT,
    CONSTRAINT fk_mpa_film FOREIGN KEY (mpa_id) REFERENCES mpa_ratings (id) ON DELETE CASCADE
);

-- Films genres Table
CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    CONSTRAINT fk_film FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
    CONSTRAINT fk_genre FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE,
    CONSTRAINT unique_film_genre UNIQUE (film_id, genre_id)
);


-- Films genres Table
CREATE TABLE IF NOT EXISTS film_mpa (
    film_id BIGINT NOT NULL UNIQUE,
    mpa_id BIGINT NOT NULL,
    CONSTRAINT fk_film_mpa FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
    CONSTRAINT fk_mpa FOREIGN KEY (mpa_id) REFERENCES mpa_ratings (id) ON DELETE CASCADE
);

-- Films likes Table
CREATE TABLE IF NOT EXISTS film_likes (
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_film_likes FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_likes FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT unique_film_user UNIQUE (film_id, user_id)
);

-- Reviews table
CREATE TABLE  IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(1000) NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    CONSTRAINT fk_film_reviews FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_reviews FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Review useful rating
CREATE TABLE  IF NOT EXISTS review_ratings (
    review_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_like BOOLEAN NOT NULL,
    CONSTRAINT fk_review FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_review_ratings FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT unique_review__ratings_user UNIQUE (review_id, user_id)
);
