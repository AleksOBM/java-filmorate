DROP ALL OBJECTS;

CREATE TABLE users (
    id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_name varchar(50),
    login varchar(50) NOT NULL,
    email varchar(50) NOT NULL,
    birthday date   NOT NULL
);

CREATE TABLE friends (
    id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id bigint NOT NULL,
    friend_id bigint NOT NULL
);

CREATE TABLE films (
    id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    film_name varchar(50) NOT NULL,
    description varchar(200) NOT NULL,
    release_date date NOT NULL,
    duration int NOT NULL,
    mpa_id int NOT NULL
);

CREATE TABLE genres (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    genre_name varchar(20) NOT NULL
);

CREATE TABLE genres_of_films (
    id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    genre_id int NOT NULL,
    film_id bigint NOT NULL
);

CREATE TABLE mpa (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    mpa_name varchar(5) NOT NULL
);

CREATE TABLE likes (
    id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id bigint NOT NULL,
    film_id bigint NOT NULL
);

CREATE TABLE reviews (
    id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    content varchar(2048) NOT NULL,
    is_positive boolean NOT NULL,
    user_id bigint NOT NULL,
    film_id bigint NOT NULL,
    useful bigint NOT NULL DEFAULT 0
);

CREATE TABLE review_likes (
    review_id bigint NOT NULL,
    user_id bigint NOT NULL,
    is_like boolean NOT NULL,
    PRIMARY KEY (review_id, user_id)
);

CREATE TABLE events (
    id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id bigint NOT NULL,
    created_at TIMESTAMP NOT NULL,
    event_type varchar(20),
    operation varchar(20),
    entity_id bigint NOT NULL
);

ALTER TABLE friends ADD CONSTRAINT fk_friends_user_id FOREIGN KEY(user_id)
REFERENCES users (id);

ALTER TABLE friends ADD CONSTRAINT fk_friends_friend_id FOREIGN KEY(friend_id)
REFERENCES users (id);

ALTER TABLE films ADD CONSTRAINT fk_films_mpa_id FOREIGN KEY(mpa_id)
REFERENCES mpa (id);

ALTER TABLE genres_of_films ADD CONSTRAINT fk_genres_of_films_genre_id FOREIGN KEY(genre_id)
REFERENCES genres (id);

ALTER TABLE genres_of_films ADD CONSTRAINT fk_genres_of_films_film_id FOREIGN KEY(film_id)
REFERENCES films (id);

ALTER TABLE likes ADD CONSTRAINT fk_likes_user_id FOREIGN KEY(user_id)
REFERENCES users (id);

ALTER TABLE likes ADD CONSTRAINT fk_likes_film_id FOREIGN KEY(film_id)
REFERENCES films (id);

ALTER TABLE reviews ADD CONSTRAINT fk_reviews_user_id FOREIGN KEY (user_id)
REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE reviews ADD CONSTRAINT fk_reviews_film_id FOREIGN KEY (film_id)
REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE review_likes ADD CONSTRAINT fk_review_likes_review_id FOREIGN KEY (review_id)
REFERENCES reviews (id);

ALTER TABLE review_likes ADD CONSTRAINT fk_review_likes_user_id FOREIGN KEY (user_id)
REFERENCES users (id);

ALTER TABLE events ADD CONSTRAINT fk_events_user_id FOREIGN KEY(user_id)
REFERENCES users (id);

CREATE INDEX IF NOT EXISTS idx_users_user_name ON users (user_name);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_login ON users (login);

CREATE INDEX IF NOT EXISTS idx_friends_user_id ON friends (user_id);
CREATE INDEX IF NOT EXISTS idx_friends_friend_id ON friends (friend_id);

CREATE INDEX IF NOT EXISTS idx_films_film_name ON films (film_name);

CREATE INDEX IF NOT EXISTS idx_reviews_film_id ON reviews (film_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON reviews (user_id);

CREATE INDEX IF NOT EXISTS idx_review_likes_review_id ON review_likes (review_id);

CREATE INDEX IF NOT EXISTS idx_events_user_id ON events (user_id);
CREATE INDEX IF NOT EXISTS idx_events_created_at ON events (created_at);