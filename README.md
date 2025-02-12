# Filmorate

### Схема БД

![](https://github.com/BuzmakovR/java-filmorate/blob/main/dbdiagram.png?raw=true)

### Примеры запросов

<details>
    <summary><h3>Для фильмов:</h3></summary>

* Создание фильма:

```SQL
-- Добавление записи с самим фильмом
INSERT INTO films (name,
                   description,
                   release_date,
                   duration,
                   mpa_rating_id)
VALUES ($1, $2, $3, $4, $5);

-- Привязка фильма к жанру
INSERT INTO films_genres (film_id, genre_id)
VALUES ($1, $2);
```

* Обновление фильма:

```SQL
UPDATE
    films
SET name                = $1,
    description         = $2,
    release_date        = $3,
    duration            = $4,
    mpa_rating_id       = $5
WHERE id = $6;
```

* Получение фильма по `id`:

```SQL
SELECT f.id,
       f.name,
       f.description,
       f.release_date,
       f.duration,
       (
            SELECT mr.name 
            FROM mpa_ratings mr 
            WHERE mr.id = f.mpa_rating_id
       ) mpa_rating,
       STRING_AGG(g.name, ', ') genres
FROM films f
LEFT JOIN films_genres fg ON fg.film_id = f.id
LEFT JOIN genres g ON g.id = fg.genre_id
WHERE f.id = $1
GROUP BY f.id;
```   

* Получение всех фильмов:

```SQL
SELECT f.id,
       f.name,
       f.description,
       f.release_date,
       f.duration,
       (
            SELECT mr.name 
            FROM mpa_ratings mr 
            WHERE mr.id = f.mpa_rating_id
       ) mpa_rating,
       STRING_AGG(g.name, ', ') genres
FROM films f
LEFT JOIN films_genres fg ON fg.film_id = f.id
LEFT JOIN genres g ON g.id = fg.genre_id
GROUP BY f.id;
```

* Получение топ-N (по количеству лайков) фильмов:
```SQL
SELECT f.id,
       f.name,
       f.description,
       f.release_date,
       f.duration,
       (
            SELECT mr.name 
            FROM mpa_ratings mr 
            WHERE mr.id = f.mpa_rating_id
       ) mpa_rating,
       STRING_AGG(DISTINCT g.name, ', ') genres,
       COUNT(DISTINCT fl.user_id) AS like_count
FROM films f
LEFT JOIN films_genres fg ON fg.film_id = f.id
LEFT JOIN genres g ON g.id = fg.genre_id
LEFT JOIN films_likes fl ON fl.film_id = f.id
GROUP BY f.id
ORDER BY like_count DESC LIMIT $1;
```

* Добавление лайка фильму:
```SQL
INSERT INTO films_likes (film_id, user_id)
VALUES ($1, $2);
```

* Удаление лайка с фильма:
```SQL
DELETE FROM films_likes 
WHERE film_id = $1
    AND user_id = $2;
```
</details>

<details>
    <summary><h3>Для пользователей:</h3></summary>

* Создание пользователя:

```SQL
INSERT INTO users (email,
                   login,
                   name,
                   birthday)
VALUES ($1, $2, $3, $4)
```

* Обновление пользователя:

```SQL
UPDATE
    users
SET email    = $1,
    login    = $2,
    name     = $3,
    birthday = $4
WHERE id = $5
```

* Получение пользователя `id`:

```SQL
SELECT *
FROM users
WHERE id = $1
```   

* Получение всех пользователей:

```SQL
SELECT *
FROM users
``` 

* Получение друзей пользователя:

```SQL
SELECT u.*
FROM users u
JOIN friend_requests fr ON fr.friend_id = u.id
WHERE fr.is_confirmed = true
    AND fr.user_id = $1
``` 

* Получение общих друзей с пользователем:

```SQL
SELECT cu.*
FROM friend_requests u1
JOIN friend_requests u2 ON u1.friend_id = u2.friend_id
JOIN users cu ON cu.id = u2.friend_id
where u1.user_id = $1 
	AND u1.is_confirmed = true
	AND u2.user_id = $2
	AND u2.is_confirmed = true
``` 

* Отправка заявки на добавление в друзья пользователя:

```SQL
INSERT INTO friend_requests (user_id,
            friend_id,
            is_confirmed)
VALUES ($1, $2, false);
```

* Принятие заявки на добавление в друзья пользователя:

```SQL
UPDATE friend_requests
SET is_confirmed = true
WHERE user_id = $1
    AND friend_id = $2;
```

* Удаление из друзей пользователя:

```SQL
DELETE FROM friend_requests
WHERE user_id = $1
    AND friend_id = $2;
```

</details>