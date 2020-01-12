use test_db;

CREATE TABLE member (
  id tinyint not null ,
  name varchar(16) not null,
  member_type enum('admin', 'general') not null,
  primary key (id)
) ENGINE=Innodb;

INSERT INTO member (id, name, member_type) VALUES
(1, "taguchi", 'admin'),
(2, "test_user", 'general')
;
