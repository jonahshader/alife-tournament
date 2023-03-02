CREATE VIEW IF NOT EXISTS leaderboard
AS
SELECT creature_id, wins, losses
FROM creature;
--ORDER BY
--	wins DEC,
--	losses ASC,
--LIMIT 20;
