-- users
create table users
(
    birth_date date         not null,
    id         bigserial    not null,
    address    varchar(255),
    email      varchar(255) not null unique,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    phone      varchar(255),
    primary key (id)
)
