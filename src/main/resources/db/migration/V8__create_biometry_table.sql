CREATE TABLE tb_biometry(
    id serial not null,
    fingerprint text not null,
    storage_date date not null,
    credit_card_id int not null,
    primary key(id),
    foreign key(credit_card_id) references tb_credit_card(id)
);