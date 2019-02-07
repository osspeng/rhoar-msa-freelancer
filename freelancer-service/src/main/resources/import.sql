---- Create the freelancer table if not present
--CREATE TABLE IF NOT EXISTS freelancer (
--  id        SERIAL PRIMARY KEY,
--  firstName      VARCHAR(40) NOT NULL,
--  lastName      VARCHAR(40) NOT NULL,
--  email     VARCHAR(40),
--  skills     VARCHAR(100)
--);
--
--DELETE FROM freelancer;

insert into freelancer(first_Name,last_Name,email,skills) values('Jason','Peng','hpeng@redhat.com','java,python');
insert into freelancer(first_Name,last_Name,email,skills) values('Eddie','Liu','eddie@redhat.com','java,python');
insert into freelancer(first_Name,last_Name,email,skills) values('Ethan','Peng','ethan@redhat.com','java,python');
