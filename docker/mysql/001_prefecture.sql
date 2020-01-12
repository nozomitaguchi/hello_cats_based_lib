use test_db;

CREATE TABLE prefecture (
  code tinyint not null,
  name varchar(16) not null,
  primary key (code)
) ENGINE=Innodb;

INSERT INTO prefecture (code, name) VALUES
(1, "hokkaido"),
(2, "aomori")
;
