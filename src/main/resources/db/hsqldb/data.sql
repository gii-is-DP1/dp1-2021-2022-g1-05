INSERT INTO achievements(id, name, description) VALUES(1, 'NombreAchievement1', 'Descripción achievement 1');
INSERT INTO achievements(id, name, description) VALUES(2, 'NombreAchievement2', 'Descripción achievement 2');
INSERT INTO achievements(id, name, description) VALUES(3, 'NombreAchievement3', 'Descripción achievement 3');

INSERT INTO users(username, password, enabled) VALUES('ManuK', 1234567, 'true');
INSERT INTO users(username, password, enabled) VALUES('marioespiro', 'admin121', 'true');
INSERT INTO users(username, password, enabled) VALUES('pedro', 'pedro121', 'true');
INSERT INTO users(username, password, enabled) VALUES('jaime', 'jaime1', 'true');
INSERT INTO users(username, password, enabled) VALUES('jose', 'jose1', 'true');
INSERT INTO users(username, password, enabled) VALUES('antonio', 'antonio1', 'true');
INSERT INTO users(username, password, enabled) VALUES('paco', 'paco1', 'true');

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

--INSERT INTO ludo_Matches(id, match_Code) VALUES(1, '111111');
--INSERT INTO goose_Matches(id, match_Code, board_id) VALUES(1, '111111', 1);

INSERT INTO achievements_players(achievements_id, players_id) VALUES(2, 1);
INSERT INTO achievements_players(achievements_id, players_id) VALUES(3, 1);



