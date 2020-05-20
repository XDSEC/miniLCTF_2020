CREATE DATABASE users;

use users;

CREATE TABLE users (
    username TEXT NOT NULL,
    password VARCHAR(200) NOT NULL
);

INSERT INTO
    users VALUE ('reclu3e', '50dc96a1567a18eb384eeddf1a9a7d48');

CREATE USER 'ctf'@'localhost' IDENTIFIED BY 'ctf';
GRANT SELECT ON users.users TO 'ctf'@'localhost';