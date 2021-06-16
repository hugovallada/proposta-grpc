ALTER TABLE tb_proposal DROP COLUMN IF EXISTS number;
ALTER TABLE tb_address ADD COLUMN number varchar(10) not null;