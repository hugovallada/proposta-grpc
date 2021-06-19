CREATE TABLE tb_lock(
    id serial not null,
    lock_timestamp date not null,
    client_ip varchar(50) not null,
    user_agent varchar(60) not null,
    credit_card_id int not null,
    primary key(id),
    foreign key(credit_card_id) references tb_credit_card(id)
);