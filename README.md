# Java-filmorate
Template repository for Filmorate project.

## DataBase project structure
![DataBase structure](https://i.ibb.co/ccDFyFH/db.png)

### DataBase queries:

- #### Film queries
  - retrieve all films
  ```
  SELECT * 
  FROM film;
  ``` 
  - retrieve a film by id
  ``` 
  SELECT * 
  FROM film
  WHERE film_id = 1;
  ``` 
  - retrieve N films (exp. N = 10)
  ``` 
  SELECT * 
  FROM film
  LIMIT 10;
  ``` 
  - retrieve a film by genre 
  ``` 
  SELECT * 
  FROM film
  WHERE genre_id = 1;
  ``` 
  - Add a film 
  ``` 
  INSERT INTO film (name, description, releaseDate, genre_id, mpaRating, duration)
  VALUES ('Example Film 1', 'A great action movie.', '2020-07-15', 1, 'PG-13', 120));
  ``` 
  - Update a film by id
  ``` 
  UPDATE film
  SET name = 'Updated Film Name'
  WHERE film_id = 1;
  ```
  - Delete a film by id
  ``` 
  DELETE FROM film
  WHERE film_id = 1;
  ```
  
- #### User queries

  - Retrieve all users
    ```
    SELECT * 
    FROM user;
    ``` 
  - Retrieve a user by id
    ``` 
    SELECT * 
    FROM user
    WHERE user_id = 1;
    ``` 
  - Retrieve N users (exp. N = 10)
    ``` 
    SELECT * 
    FROM user
    LIMIT 10;
    ```  
  - Add a user
    ``` 
    INSERT INTO user (email, login, name, birthday)
    VALUES ('example@example.com', 'exampleLogin', 'Example User', '1990-01-01');
    ``` 
  - Update a user by id
    ``` 
    UPDATE user
    SET name = 'Updated User Name'
    WHERE user_id = 1;
    ```
  - Delete a user by id
    ``` 
    DELETE FROM user
    WHERE user_id = 1;
    ```

- #### Friendship queries

  - Retrieve all friendships
  ```
  SELECT * 
  FROM friendship;
  ``` 
  - Retrieve friendships for a specific user by user_id
  ``` 
  SELECT * 
  FROM friendship
  WHERE first_friend_id = 1 OR second_friend_id = 1;

  ``` 
  - Add a friendship
  ``` 
  INSERT INTO friendship (first_friend_id, second_friend_id, status)
  VALUES (1, 2, 'PENDING');
  ```  
  - Update a friendship
  ``` 
  UPDATE friendship
  SET status = 'CONFIRMED'
  WHERE first_friend_id = 1 AND second_friend_id = 2;
  ```
  - Delete a user by id
  ``` 
  DELETE FROM friendship
  WHERE (first_friend_id = 1 AND second_friend_id = 2)
     OR (first_friend_id = 2 AND second_friend_id = 1);
  ```
    


