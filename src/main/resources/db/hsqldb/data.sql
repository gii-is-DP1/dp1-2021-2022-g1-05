INSERT INTO achievements(id, name, description) VALUES(1, 'NombreAchievement1', 'Descripción achievement 1');
INSERT INTO achievements(id, name, description) VALUES(2, 'NombreAchievement2', 'Descripción achievement 2');
INSERT INTO achievements(id, name, description) VALUES(3, 'NombreAchievement3', 'Descripción achievement 3');

INSERT INTO users(id, username, password, enabled) VALUES(1, 'ManuK', 1234567, 'true');

INSERT INTO players(id, email, username) VALUES(1, 'manu@gmail.com', 'ManuK');

INSERT INTO authorities(id, authority, user_id) VALUES(1, 'player', 1);

INSERT INTO achievements_players(achievements_id, players_id) VALUES(2, 1);
INSERT INTO achievements_players(achievements_id, players_id) VALUES(3, 1);


