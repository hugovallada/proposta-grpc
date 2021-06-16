CREATE TABLE address(
    id serial not null,
    city varchar(30) not null,
    email varchar(60) not null,
    state varchar(30) not null,
    cep varchar(10) not null,
    extension varchar(50),
    primary key(id)
);

CREATE TABLE proposal(
    id serial not null,
    document varchar(20) not null,
    name varchar(30) not null,
    number varchar(10) not null,
    address_id int not null,
    salary float not null,
    external_id uuid not null,
    primary key(id),
    foreign key(address_id) references address(id)
);