# java-filmorate
### Описание (Description)  
The Filmorate project — это приложение для работы с базой данных фильмов.
### Основные возможности
- Хранение данных о фильмах
- Хранение данных о пользователях
- Возможность пользователям ставить лайки фильмам
- Возможность пользователям добавлять друг друга в друзья
- Возможность получать рейтинг TOP_N фильмов по количеству лайков
- Возможность получать общих с другим пользователем фильмов с сортировкой по их популярности
- Фильмы содержат возрастные ограничения по стандарту MPPA
- Реализована система жанров для фильмов
### Требования (Environmental Requirements) 
Для запуска проекта требуется: - Java версии 21  
Приложение работает на порту 8080
### Установка и запуск (Installation and Running) 
Откройте терминал в корне проекта и выполните команды для запуска приложения.   

**Вариант 1 Spring**  
 `mvn spring-boot:run`  
 - для остановки CTRL+C  

**Вариант 2 Java**  
`mvn package`  
`cd ./target/`  
`java -jar filmorate-0.0.1-SNAPSHOT.jar`  
- для остановки CTRL+C  

**Вариант 3 Docker**   
`mvn package`  
`sudo docker build -t filmorate-image .`  
`sudo docker run --name filmorate -p 8080:8080 filmorate-image`  
- для остановки `sudo docker stop filmorate`  
### Работа с приложением (Working with the app)
Для работы с приложением используйте REST-клиент
- Postman
- Insomnia
- RestFox
- и подобные
### Примеры пользовательских запросов (Request Examples)
![ExamplesOfRequests](ExamplesOfRequests.png)
### Архитектура и структура проекта (Architecture and Project Structure) 
Проект использует Spring Boot версии 3.5.9 и включает в себя следующие основные зависимости:
- spring-boot-starter-web 
- spring-boot-starter-test 
- spring-boot-starter-validation 
- spring-boot-starter-jdbc 
- h2database - lombok 
- logback-classic 
- logbook-spring-boot-starter
### Схема базы данных (Database map)
![DatabaseMap](QuickDBD-FILMO_RATE.png "Database map:")
### Примеры запросов к базе данных:
1. Get TOP-10 films:
```sql
SELECT f.film_name,
       sum(l.user_id) AS rate
FROM films f
         JOIN likes l ON f.id = l.film_id
GROUP BY f.film_name
ORDER BY rate DESC
    LIMIT 10;
```

2. Get all the movies liked by users 5, 6 and 7 with a
   duration of more than 100 minutes and with genres of
   adventure, horror, action.
```sql
SELECT f.film_name,
       f.release_date,
       f.duration
FROM films f
WHERE f.id IN
      (SELECT l.film_id
       FROM likes l
       WHERE l.user_id IN
             (SELECT friend_id
              FROM friends fr
              WHERE fr.user_id IN (5, 6, 7)
             )
      )
  AND f.duration > 100
  AND f.id IN
      (SELECT fg.film_id
       FROM genres_of_films fg
       WHERE fg.genre_id IN
             (SELECT g.id
              FROM genres g
              WHERE g.genre_name
                       IN('Боевик', 'Триллер', 'Комедия')
             )
      );
```
