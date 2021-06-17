CREATE TABLE tb_expiration_date(
    id varchar(60) not null,
    day int not null,
    issued_date date not null,
    primary key(id)
);


CREATE TABLE tb_credit_card(
    id serial not null,
    number varchar(60) unique,
    issued_date date not null,
    owner varchar(60) not null,
    credit_limit float not null,
    expiration_date_id varchar(60) not null,
    primary key(id),
    foreign key(expiration_date_id) references tb_expiration_date(id)
);