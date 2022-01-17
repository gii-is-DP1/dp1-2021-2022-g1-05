INSERT INTO achievements(id, name, description, number_to_beat) VALUES(1, 'Walk 20 times on goose square', 'Number of times landed on goose squares', 20);
INSERT INTO achievements(id, name, description, number_to_beat) VALUES(2, 'Walk 100 times on goose square', 'Number of times landed on goose squares', 100);
INSERT INTO achievements(id, name, description, number_to_beat) VALUES(3, 'Win 20 goose games', 'Number of goose games won', 20);
INSERT INTO achievements(id, name, description, number_to_beat) VALUES(4, 'Walk 200 times on ludo squares', 'Number of walked squares', 200);
INSERT INTO achievements(id, name, description, number_to_beat) VALUES(5, 'Eat 10 tokens', 'Number of eaten tokens', 10);
INSERT INTO achievements(id, name, description, number_to_beat) VALUES(6, 'Win 20 ludo games', 'Number of ludo games won', 20);
INSERT INTO achievements(id, name, description, number_to_beat) VALUES(7, 'Walk 300 times on dice square', 'Number of times landed on dice squares', 300);

INSERT INTO users(username, password, enabled) VALUES('ManuK', '1234567', 'true');
INSERT INTO users(username, password, enabled) VALUES('marioespiro', 'admin121', 'true');
INSERT INTO users(username, password, enabled) VALUES('pedro', 'pedro121', 'true');
INSERT INTO users(username, password, enabled) VALUES('jaime', 'jaime1', 'true');
INSERT INTO users(username, password, enabled) VALUES('jose', 'jose1', 'true');
INSERT INTO users(username, password, enabled) VALUES('antonio', 'antonio1', 'true');
INSERT INTO users(username, password, enabled) VALUES('paco', 'paco1', 'false');

INSERT INTO players(id, email, username) VALUES(1, 'manu@gmail.com', 'ManuK');
INSERT INTO players(id, email, username) VALUES(2, 'mario@testmail.com', 'marioespiro');
INSERT INTO players(id, email, username) VALUES(3, 'pedro@pedromail.com', 'pedro');
INSERT INTO players(id, email, username) VALUES(4, 'jaime@domain.com', 'jaime');
INSERT INTO players(id, email, username) VALUES(5, 'jose@domain.com', 'jose');
INSERT INTO players(id, email, username) VALUES(6, 'antonio@domain.com', 'antonio');
INSERT INTO players(id, email, username) VALUES(7, 'paco@domain.com', 'paco');

INSERT INTO authorities(id, authority, username) VALUES(1, 'admin', 'ManuK');
INSERT INTO authorities(id, authority, username) VALUES(2, 'admin', 'marioespiro');
INSERT INTO authorities(id, authority, username) VALUES(3, 'player', 'pedro');
INSERT INTO authorities(id, authority, username) VALUES(4, 'player', 'jaime');
INSERT INTO authorities(id, authority, username) VALUES(5, 'player', 'jose');
INSERT INTO authorities(id, authority, username) VALUES(6, 'player', 'antonio');
INSERT INTO authorities(id, authority, username) VALUES(7, 'player', 'paco');




