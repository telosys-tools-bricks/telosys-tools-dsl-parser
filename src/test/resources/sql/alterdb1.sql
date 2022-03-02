-- CREATE SCHEMA IF NOT EXISTS TEST1;

-- SET SCHEMA TEST1 ;
-- SET SCHEMA SCHEMA1 ;

-- 1 entity created
CREATE TABLE badge ( 
	code VARCHAR(2),
	name VARCHAR(45),
	PRIMARY KEY(code)
);

-- 1 entity deleted
DROP TABLE CUSTOMER ;

-- 1 entity updated
ALTER TABLE country ADD COLUMN badge_code VARCHAR(2);
ALTER TABLE country ADD FOREIGN KEY(badge_code) REFERENCES badge(code) ;

