CREATE TABLE tb_credit_card_associate_wallet(
    credit_card_id int not null,
    wallet_id int not null,
    foreign key(credit_card_id) references tb_credit_card(id),
    foreign key(wallet_id) references tb_wallet(id)
);