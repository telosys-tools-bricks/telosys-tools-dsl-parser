-- CREATE SCHEMA IF NOT EXISTS TEST4;

-- SET SCHEMA TEST4 ;
DROP ALL OBJECTS;

-- Many To Many

CREATE TABLE teacher (
  code INTEGER NOT NULL,
  name VARCHAR(40),
  student VARCHAR(20),
  list_Of_Student VARCHAR(10),
  PRIMARY KEY(code)
);

CREATE TABLE student (
  id INTEGER NOT NULL ,
  first_name VARCHAR(40),
  last_name VARCHAR(40),
  teacher_code1 INTEGER,
  teacher_code2 INTEGER,
  teacher VARCHAR(20),
  list_Of_teacher VARCHAR(10),
  PRIMARY KEY(id),
  FOREIGN KEY(teacher_code1) REFERENCES teacher(code),
  FOREIGN KEY(teacher_code2) REFERENCES teacher(code)
);

CREATE TABLE relation1 (
  student_id   INTEGER NOT NULL ,
  teacher_code INTEGER NOT NULL,
  PRIMARY KEY(student_id, teacher_code),
  FOREIGN KEY(student_id)   REFERENCES student(id),
  FOREIGN KEY(teacher_code) REFERENCES teacher(code)
);
