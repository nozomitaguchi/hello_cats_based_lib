use test_db;

CREATE TABLE station (
  code tinyint not null,
  name varchar(16) not null,
  prefecture_code tinyint not null,
  primary key (code)
) ENGINE=Innodb;

INSERT INTO station (code, name, prefecture_code) VALUES
(1, "sapporo", 1)
;
