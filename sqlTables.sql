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
    foreign key (username) references  Users(username) on update cascade on delete cascade
);

Create table Books(
    isbn text not null,
    title text not null ,
    authorUsername text not null,
    imageUrl text default null,
    description text null default null,
    publisher text default null,
    publishedDate text default null,
    pageCount integer default 0,
    genre text null default  null,
    previewLink text default null,
    price float(5,2) null default  null,
    stock int check ( stock >= 0 ) not null default  0,
    primary key (isbn),
    foreign key (authorUsername) references  Users(username)
);

create  table Comments(
    commentID integer primary key autoIncrement,
    username text not null,
    dateCreated Datetime,
    content text default '',
    bookIsbn text not null,
    replyTo integer default null,
    rating integer default 0,
    foreign key (username) references  Users(username),
    foreign key (bookIsbn) references  Books(isbn),
    foreign key (replyTo) references Comments(commentID)
);

create table  Orders(
    orderId integer primary key autoincrement,
    created Datetime,
    status text check(status in ('preparing', 'sent', 'delivered', 'cancelled')),
    total float(6,2) not null,
    address text not null,
    username text not null,
    foreign key (username) references Users(username)
);

create  table  OrderItems(
    orderId integer,
    bookIsbn text,
    quantity integer,
    price float(6,2) not null,
    primary key (orderId,bookIsbn),
    foreign key (orderId) references  Orders(orderId),
    foreign key (bookIsbn) references  Books(isbn)
);

create  table  Tickets(
    ticketID integer primary key autoincrement,
    comment text,
    username text not null,
    creationDate datetime,
    status text check ( status in ('opened','resolved') ),
    foreign key (username) references Users(username)
)