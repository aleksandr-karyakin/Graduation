DELETE
FROM user_roles;
DELETE
FROM votes;
DELETE
FROM dishes;
DELETE
FROM restaurants;
DELETE
FROM users;

ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', '{noop}password'),
       ('Admin', 'admin@gmail.com', '{noop}admin'),
       ('Manager', 'manager@gmail.com', '{noop}manager');

INSERT INTO restaurants (name, user_id)
VALUES ('McDonalds', 100002),
       ('KFC', 100002);

INSERT INTO dishes (name, price, enabled, restaurant_id)
VALUES ('burger', '100', true, 100003),
       ('coffee', '50', false, 100003),
       ('chicken', '120', true, 100004),
       ('tea', '60', true, 100004);

INSERT INTO votes (user_id, restaurant_id)
VALUES (100001, 100004),
       (100002, 100003);

INSERT INTO user_roles (role, user_id)
VALUES ('ROLE_USER', 100000),
       ('ROLE_USER', 100001),
       ('ROLE_MANAGER', 100001),
       ('ROLE_ADMIN', 100001),
       ('ROLE_USER', 100002),
       ('ROLE_MANAGER', 100002);
