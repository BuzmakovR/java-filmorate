CREATE TABLE IF NOT EXISTS friend_requests (
  user_id bigint,
  friend_id bigint,
  is_confirmed boolean DEFAULT FALSE,
  PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS users (
  id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  login varchar(255) NOT NULL,
  email varchar(255) NOT NULL,
  name varchar(255),
  birthday date
);

CREATE TABLE IF NOT EXISTS films (
  id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar(255) NOT NULL,
  description varchar(255),
  release_date date,
  duration integer,
  mpa_rating_id bigint
);

CREATE TABLE IF NOT EXISTS films_likes (
  film_id bigint,
  user_id bigint,
  PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
  id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
  id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films_genres (
  film_id bigint,
  genre_id bigint,
  PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS directors (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_directors (
    film_id BIGINT NOT NULL,
    director_id BIGINT NOT NULL,
    CONSTRAINT fk_film_directors FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
    CONSTRAINT fk_director FOREIGN KEY (director_id) REFERENCES directors (id) ON DELETE CASCADE,
    CONSTRAINT unique_film_director UNIQUE (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id bigint NOT NULL,
    user_id bigint NOT NULL,
    is_positive boolean DEFAULT TRUE,
    content varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews_likes (
  review_id bigint,
  user_id bigint,
  is_like boolean DEFAULT TRUE,
  PRIMARY KEY (review_id, user_id)
);

ALTER TABLE users ADD CONSTRAINT IF NOT EXISTS uq_users_login UNIQUE (login);

ALTER TABLE films ADD FOREIGN KEY (mpa_rating_id) REFERENCES mpa_ratings (id) ON DELETE SET NULL;

ALTER TABLE films_likes ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE;
ALTER TABLE films_likes ADD FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE films_genres ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE;
ALTER TABLE films_genres ADD FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE;

ALTER TABLE friend_requests ADD FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE friend_requests ADD FOREIGN KEY (friend_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE reviews ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE;
ALTER TABLE reviews ADD FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE reviews_likes ADD FOREIGN KEY (review_id) REFERENCES reviews (review_id) ON DELETE CASCADE;
ALTER TABLE reviews_likes ADD FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
