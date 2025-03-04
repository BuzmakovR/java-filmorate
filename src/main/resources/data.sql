MERGE INTO users AS t
USING (
	SELECT 'user-1' login, 'user-1@mail.ru' email, 'user-1' name FROM dual UNION ALL
	SELECT 'user-2', 'user-2@mail.ru', 'user-2' FROM dual UNION ALL
	SELECT 'user-3', 'user-3@mail.ru', 'user-3' FROM dual
) d ON (t.login = d.login)
WHEN NOT MATCHED THEN INSERT(login, email, name) VALUES(d.login, d.email, d.name);

MERGE INTO friend_requests AS t
USING (
    SELECT
        (SELECT id FROM users WHERE login = 'user-1') user_id,
        (SELECT id FROM users WHERE login = 'user-2') friend_id,
        true is_confirmed
    FROM dual
    UNION ALL
    SELECT
        (SELECT id FROM users WHERE login = 'user-2'),
        (SELECT id FROM users WHERE login = 'user-1'),
        true
    FROM dual
    UNION ALL
    SELECT
        (SELECT id FROM users WHERE login = 'user-2'),
        (SELECT id FROM users WHERE login = 'user-3'),
        false
    FROM dual
    UNION ALL
    SELECT
        (SELECT id FROM users WHERE login = 'user-1'),
        (SELECT id FROM users WHERE login = 'user-3'),
        false
    FROM dual
) d ON (t.user_id = d.user_id AND t.friend_id = d.friend_id)
WHEN NOT MATCHED THEN INSERT(user_id, friend_id, is_confirmed) VALUES(d.user_id, d.friend_id, d.is_confirmed);

MERGE INTO mpa_ratings AS t
USING (
    SELECT 'G' name FROM dual UNION ALL
    SELECT 'PG' name FROM dual UNION ALL
    SELECT 'PG-13' name FROM dual UNION ALL
    SELECT 'R' name FROM dual UNION ALL
    SELECT 'NC-17' name FROM dual
) d ON (t.name = d.name)
WHEN NOT MATCHED THEN INSERT(name) VALUES(d.name);

MERGE INTO films AS t
USING (
    SELECT 'film-1' name, 'description-1' description, 90 duration, (SELECT id FROM mpa_ratings WHERE name = 'R') mpa_rating_id FROM dual
    UNION ALL
    SELECT 'film-2', 'description-2', 90, (SELECT id FROM mpa_ratings WHERE name = 'R') FROM dual
) d ON (t.name = d.name)
WHEN NOT MATCHED THEN INSERT(name, description, duration, mpa_rating_id) VALUES(d.name, d.description, d.duration, d.mpa_rating_id);

MERGE INTO genres AS t
USING (
    SELECT 'Комедия' name FROM dual UNION ALL
    SELECT 'Драма' name FROM dual UNION ALL
    SELECT 'Мультфильм' name FROM dual UNION ALL
    SELECT 'Триллер' name FROM dual UNION ALL
    SELECT 'Документальный' name FROM dual UNION ALL
    SELECT 'Боевик' name FROM dual
) d ON (t.name = d.name)
WHEN NOT MATCHED THEN INSERT(name) VALUES(d.name);

MERGE INTO films_genres AS t
USING (
    SELECT
        (SELECT id FROM films WHERE name = 'film-1') film_id,
        (SELECT id FROM genres WHERE name = 'Триллер') genre_id
    FROM dual
    UNION ALL
    SELECT
        (SELECT id FROM films WHERE name = 'film-1'),
        (SELECT id FROM genres WHERE name = 'Комедия')
    FROM dual
    UNION ALL
    SELECT
        (SELECT id FROM films WHERE name = 'film-2'),
        (SELECT id FROM genres WHERE name = 'Комедия')
    FROM dual
) d ON (t.film_id = d.film_id AND t.genre_id = d.genre_id)
WHEN NOT MATCHED THEN INSERT(film_id, genre_id) VALUES(d.film_id, d.genre_id);

--MERGE INTO films_likes AS t
--USING (
--    SELECT
--        (SELECT id FROM films WHERE name = 'film-1') film_id,
--        (SELECT id FROM users WHERE login = 'user-1') user_id
--    FROM dual
--    UNION ALL
--    SELECT
--        (SELECT id FROM films WHERE name = 'film-2'),
--        (SELECT id FROM users WHERE login = 'user-3')
--    FROM dual
--    UNION ALL
--    SELECT
--        (SELECT id FROM films WHERE name = 'film-2'),
--        (SELECT id FROM users WHERE login = 'user-1')
--    FROM dual
--) d ON (t.film_id = d.film_id AND t.user_id = d.user_id)
--WHEN NOT MATCHED THEN INSERT(film_id, user_id) VALUES(d.film_id, d.user_id);
