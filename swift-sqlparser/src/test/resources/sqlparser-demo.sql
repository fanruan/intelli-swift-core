select * from a;

SELECT a, todate(b, 'yyyy-m-d'), SUM(c)
FROM t
WHERE (c1 = 100 AND c2 LIKE '%as%') OR (c3 BETWEEN 10 AND 100 OR Todate(d) > toDate(e)) and not 1 > 2
GROUP BY z, a
HAVING AVG(c9) > 100
ORDER BY c4 DESC, c5
LIMIT 10;

CREATE TABLE a (c1 INTEGER, c2 DOUBLE, c3 VARCHAR(10), c4 DATE);

DROP TABLE a;

ALTER TABLE a ADD c5 BIT, c6 time;
ALTER TABLE a DROP c1, c2;

INSERT INTO a (c2, c3, c4, c5) VALUES (1., 'string', NULL, 0);
INSERT INTO a VALUES (1., 'string', NULL, 0);

DELETE FROM a WHERE c2 LIKE '%as%' OR c3 BETWEEN 10 AND 100
