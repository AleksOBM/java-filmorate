CREATE TABLE IF NOT EXISTS "user" (
    "id" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "user_name" varchar(50) NOT NULL,
    "login" varchar(30) NOT NULL,
    "email" varchar(30) NOT NULL,
    "birthday" date NOT NULL
);

CREATE TABLE IF NOT EXISTS "friends" (
    "id" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "user_id" int NOT NULL,
    "friend_id" int NOT NULL
);

CREATE TABLE IF NOT EXISTS "film" (
    "id" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "film_name" varchar(50) not null,
    "description" varchar(200)   NOT NULL,
    "release_date" date   NOT NULL,
    "duration" int   NOT NULL,
    "age_rating_id" int   NOT NULL
);

CREATE TABLE IF NOT EXISTS "genre" (
    "id" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "genre_name" varchar(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS "genres_of_films" (
    "id" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "genre_id" int NOT NULL,
    "film_id" int NOT null
);

CREATE TABLE IF NOT EXISTS "age_rating" (
    "id" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "age_rating_name" varchar(5) NOT NULL
);

CREATE TABLE IF NOT EXISTS "likes" (
    "id" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "user_id" int NOT NULL,
    "film_id" int NOT NULL
);

ALTER table "friends" ADD constraint IF NOT EXISTS "fk_friends_user_id"
FOREIGN KEY("user_id") REFERENCES "user" ("id");

ALTER TABLE "friends" ADD CONSTRAINT IF NOT EXISTS "fk_friends_friend_id"
FOREIGN KEY("friend_id") REFERENCES "user" ("id");

ALTER TABLE "film" ADD CONSTRAINT IF NOT EXISTS "fk_film_age_rating_id"
FOREIGN KEY("age_rating_id") REFERENCES "age_rating" ("id");

ALTER TABLE "genres_of_films" ADD CONSTRAINT IF NOT EXISTS "fk_genres_of_films_genre_id"
FOREIGN KEY("genre_id") REFERENCES "genre" ("id");

ALTER TABLE "genres_of_films" ADD CONSTRAINT IF NOT EXISTS "fk_genres_of_films_film_id"
FOREIGN KEY("film_id") REFERENCES "film" ("id");

ALTER TABLE "likes" ADD CONSTRAINT IF NOT EXISTS "fk_likes_user_id"
FOREIGN KEY("user_id") REFERENCES "user" ("id");

ALTER TABLE "likes" ADD CONSTRAINT IF NOT EXISTS "fk_likes_film_id"
FOREIGN KEY("film_id") REFERENCES "film" ("id");
