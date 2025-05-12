create table  Users(
    username text not null,
    password text not null,
    name text not null,
    surname text not null ,
    active boolean not null default true,
    role  check ( role in ('Admin','Customer','Author')) not null,
    primary key (username)
);

create table CustomerAddress(
    username text not null,
    address text not null,
    primary key (username),
    foreign key (username) references  Users(username)
);

Create table Books(
    isbn text not null,
    title text not null ,
    authorUsername text not null,
    genre text null default  null,
    price float(5,2) null default  null,
    stock int check ( stock >= 0 ) not null default  0,
    primary key (isbn),
    foreign key (authorUsername) references  Users(username)
);