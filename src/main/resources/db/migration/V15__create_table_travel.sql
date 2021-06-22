CREATE TABLE tb_travel(
    id serial primary key,
    destination varchar(100) not null,
    return_date date not null,
    notice_date date not null,
    client_ip varchar(15) not null,
    user_agent varchar(40) not null,
    credit_card_id int not null,
    foreign key (credit_card_id) references tb_credit_card(id)
);