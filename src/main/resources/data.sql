-- users
merge into "user" ("user_name", "login", "email", "birthday") key("email") values('Victor Schmidt', 'schmidt777', 'Fannie.Gerlach61@yahoo.com', '1965-07-13');
merge into "user" ("user_name", "login", "email", "birthday") key("email") values('Paula Hansen', 'paula-hansen', 'Kaley_Lowe23@gmail.com', '2007-11-06');
merge into "user" ("user_name", "login", "email", "birthday") key("email") values('Hugo Kuphal', 'HHugo', 'Newell_Rau@hotmail.com', '1983-10-31');
merge into "user" ("user_name", "login", "email", "birthday") key("email") values('Flora Barton', 'lora', 'Rod57@gmail.com', '1983-08-16');
merge into "user" ("user_name", "login", "email", "birthday") key("email") values('Olivia Dietrich', 'Odietrich', 'Prudence.Smith92@gmail.com', '1961-09-29');
merge into "user" ("user_name", "login", "email", "birthday") key("email") values('Frederick Kunze', 'Kyanz', 'Lenora81@gmail.com', '1968-09-30');
merge into "user" ("user_name", "login", "email", "birthday") key("email") values('Erick Gutmann', 'gutmann11', 'Josiane.Pouros@yahoo.com', '1962-05-11');
merge into "user" ("user_name", "login", "email", "birthday") key("email") values('Naomi Cole I', 'nency', 'Lola23@hotmail.com', '2003-09-23');
merge into "user" ("user_name", "login", "email", "birthday") key("email") values('Doris Cronin', 'Doronin', 'Lorenz.Kreiger67@gmail.com', '1969-05-07');
merge into "user" ("user_name", "login", "email", "birthday") key("email") values('Gabriel Smitham', 'GSmitham', 'Tre_Torp99@yahoo.com', '1968-04-15');


-- age_ratings
merge into "age_rating" ("age_rating_name") key("age_rating_name") values('G');
merge into "age_rating" ("age_rating_name") key("age_rating_name") values('PG');
merge into "age_rating" ("age_rating_name") key("age_rating_name") values('PG-13');
merge into "age_rating" ("age_rating_name") key("age_rating_name") values('R');
merge into "age_rating" ("age_rating_name") key("age_rating_name") values('NC-17');


-- films
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(1, 'Король Лев', 'The Circle of Life', '1994-06-12', '88');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(1, 'ВАЛЛ-И', 'Любовь - дело техники', '2008-06-21', '98');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(1, 'Земля: Один потрясающий день', 'Один день. Одна планета. Невероятные чудеса', '2017-08-11', '95');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(1, 'Унесённые ветром', 'The most magnificent picture ever!', '1939-01-15', '212');

merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(2, 'Назад в будущее', 'Семнадцатилетний Марти МакФлай пришел вчера домой пораньше. На 30 лет раньше', '1985-06-03', '116');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(2, 'Унесённые призраками', 'The tunnel led Chihiro to a mysterious town...', '2001-07-20', '124');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(2, 'Любовное настроение', 'Feel the heat, keep the feeling burning, let the sensation explode', '2000-05-20', '98');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(2, 'Дэвид Аттенборо: Жизнь на нашей планете', 'He introduced us to the world. Now he tells his greatest story', '2020-09-28', '83');

merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(3, 'Начало', 'Твой разум - место преступления', '2010-06-08', '148');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(3, 'Древо жизни', 'Our picture is a cosmic epic, a hymn to life', '2011-05-16', '139');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(3, 'Форрест Гамп', 'Мир уже никогда не будет прежним, после того как вы увидите его глазами Форреста Гампа', '1994-06-23', '132');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(3, 'Интерстеллар', 'Следующий шаг человечества станет величайшим', '2014-04-26', '169');

merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(4, 'Малхолланд Драйв', 'Beware what you dream for...', '2003-05-16', '147');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(4, 'Нефть', 'И будет кровь', '2007-09-27', '158');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(4, 'Вечное сияние чистого разума', 'Можно стереть любовь из памяти. Выкинуть из сердца - это уже другая история', '2004-03-09', '106');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(4, 'Старикам тут не место', 'Из воды сухим не выйти', '2007-05-19', '122');

merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(5, 'Вожделение', 'The Trap is Set', '2007-08-30', '157');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(5, 'Святая кровь', 'Forget Everything You Have Ever Seen', '1989-06-10', '123');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(5, 'Жизнь Адель', 'Синий - самый теплый цвет', '2013-05-23', '179');
merge into "film" ("age_rating_id", "film_name", "description", "release_date", "duration") key("film_name")
values(5, 'Свяжи меня', 'A love story...with strings attached!', '1989-01-12', '101');


-- genres
merge into "genre" ("genre_name") key("genre_name") values('family');
merge into "genre" ("genre_name") key("genre_name") values('adventure');
merge into "genre" ("genre_name") key("genre_name") values('animated');
merge into "genre" ("genre_name") key("genre_name") values('comedy');
merge into "genre" ("genre_name") key("genre_name") values('drama');
merge into "genre" ("genre_name") key("genre_name") values('fantasy');
merge into "genre" ("genre_name") key("genre_name") values('historical');
merge into "genre" ("genre_name") key("genre_name") values('horror');
merge into "genre" ("genre_name") key("genre_name") values('melodrama');
merge into "genre" ("genre_name") key("genre_name") values('thriller');
merge into "genre" ("genre_name") key("genre_name") values('action');
merge into "genre" ("genre_name") key("genre_name") values('detective');
merge into "genre" ("genre_name") key("genre_name") values('documentary');


-- "genres_of_films"
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (1, 1);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (1, 3);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (1, 6);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (1, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (1, 2);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (2, 1);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (2, 2);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (2, 3);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (2, 6);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (3, 13);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (4, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (4, 7);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (4, 9);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (5, 1);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (5, 2);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (5, 4);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (5, 6);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (6, 1);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (6, 2);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (6, 3);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (6, 6);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (7, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (7, 9);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (8, 13);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (9, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (9, 6);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (9, 10);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (9, 11);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (9, 12);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (10, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (10, 6);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (11, 4);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (11, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (11, 7);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (11, 9);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (12, 2);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (12, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (12, 6);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (13, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (13, 10);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (13, 12);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (14, 5);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (15, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (15, 6);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (15, 9);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (16, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (16, 10);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (17, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (17, 7);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (17, 9);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (17, 10);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (18, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (18, 8);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (18, 10);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (19, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (19, 9);

merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (20, 4);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (20, 5);
merge into "genres_of_films" ("film_id", "genre_id") key("film_id", "genre_id") values (20, 9);


-- "likes"
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (1, 2);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (1, 3);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (1, 4);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (1, 6);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (1, 8);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (1, 9);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (1, 10);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (1, 12);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (1, 14);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (1, 17);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (1, 19);

merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (2, 20);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (2, 16);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (2, 13);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (2, 4);

merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (3, 14);

merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (4, 17);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (4, 18);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (4, 19);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (4, 15);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (4, 7);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (4, 5);

merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (5, 8);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (5, 9);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (5, 10);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (5, 11);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (5, 15);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (5, 19);

merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (6, 2);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (6, 4);

merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (8, 3);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (8, 5);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (8, 6);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (8, 19);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (8, 13);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (8, 15);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (8, 10);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (8, 9);

merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (9, 6);
merge into "likes" ("user_id", "film_id") key("user_id", "film_id") values (9, 7);


--"friends"
merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (1, 4);
merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (4, 1);

merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (1, 8);
merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (8, 1);

merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (2, 3);
merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (3, 2);

merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (3, 4);
merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (4, 3);

merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (3, 5);
merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (5, 3);

merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (3, 9);
merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (9, 3);

merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (5, 7);
merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (7, 5);

merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (5, 1);
merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (1, 5);

merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (5, 4);
merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (4, 5);

merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (5, 7);
merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (7, 5);

merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (6, 4);
merge into "friends" ("user_id", "friend_id") key("user_id", "friend_id") values (4, 6);

