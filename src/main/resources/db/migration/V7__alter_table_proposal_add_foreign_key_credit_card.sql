ALTER TABLE tb_proposal ADD COLUMN credit_card_id int;

ALTER TABLE tb_proposal ADD CONSTRAINT fk_credit_card_id foreign key(credit_card_id) references tb_credit_card(id);