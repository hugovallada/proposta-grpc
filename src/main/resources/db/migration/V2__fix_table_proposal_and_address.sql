ALTER TABLE address DROP COLUMN IF EXISTS email;
ALTER TABLE proposal ADD COLUMN email varchar(60) not null;

ALTER TABLE address RENAME TO tb_address;
ALTER TABLE proposal RENAME TO tb_proposal;