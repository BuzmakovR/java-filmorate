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
VALUES (?, ?, ?, ?, ?);

-- Привязка фильма к жанру
INSERT INTO films_genres (film_id, genre_id)
VALUES (?, ?);
```

* Обновление фильма:

```SQL
-- Обновление записи с самим фильмом
UPDATE
    films
SET name                = ?,
    description         = ?,
    release_date        = ?,
    duration            = ?,
    mpa_rating_id       = ?
WHERE id = ?;

-- Обновление жанров фильма
DELETE FROM films_genres
WHERE film_id = ?
    AND genre_id = ?;

INSERT INTO films_genres (film_id, genre_id)
VALUES (?, ?);
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
WHERE f.id = ?
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
ORDER BY like_count DESC LIMIT ?;
```

* Добавление лайка фильму:
```SQL
INSERT INTO films_likes (film_id, user_id)
VALUES (?, ?);
```

* Удаление лайка с фильма:
```SQL
DELETE FROM films_likes 
WHERE film_id = ?
    AND user_id = ?;
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
VALUES (?, ?, ?, ?)
```

* Обновление пользователя:

```SQL
UPDATE
    users
SET email    = ?,
    login    = ?,
    name     = ?,
    birthday = ?
WHERE id = ?
```

* Получение пользователя `id`:

```SQL
SELECT *
FROM users
WHERE id = ?
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
    AND fr.user_id = ?
``` 

* Получение общих друзей с пользователем:

```SQL
SELECT cu.*
FROM friend_requests u1
JOIN friend_requests u2 ON u1.friend_id = u2.friend_id
JOIN users cu ON cu.id = u2.friend_id
where u1.user_id = ? 
	AND u1.is_confirmed = true
	AND u2.user_id = ?
	AND u2.is_confirmed = true
``` 

* Отправка заявки на добавление в друзья пользователя:

```SQL
INSERT INTO friend_requests (user_id,
            friend_id,
            is_confirmed)
VALUES (?, ?, false);
```

* Принятие заявки на добавление в друзья пользователя:

```SQL
UPDATE friend_requests
SET is_confirmed = true
WHERE user_id = ?
    AND friend_id = ?;
```

* Удаление из друзей пользователя:

```SQL
DELETE FROM friend_requests
WHERE user_id = ?
    AND friend_id = ?;
```

</details>