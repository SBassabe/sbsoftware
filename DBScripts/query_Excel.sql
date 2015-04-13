------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- Excel Queries Brainstorm
------------------------------------------------------------------------------------------------------------------------------------------------------------------------

--- To extract a specific date to popolate Excel
SELECT NVL(B.CVALUE, A.DEFAULT_VAL) AS CALC_VAL,  A.*, B.*
  FROM LOCATION A 
       LEFT JOIN CLEANING_SCHEDULE B ON A.LOC_ID = B.LOC_ID
WHERE B.CDATE = '2011-01-01' OR B.CDATE IS NULL;





------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- Rubish
------------------------------------------------------------------------------------------------------------------------------------------------------------------------

SELECT  A.*, D.*
   FROM LOCATION A
              FULL OUTER JOIN (SELECT B.COD_DATA, C.LOC_ID, C.CVALUE  FROM TEMPO B LEFT JOIN CLEANING_SCHEDULE C ON B.COD_DATA = C.CDATE) AS D ON A.LOC_ID = D.LOC_ID
WHERE A.FLOOR_ID = 'PV';

/*
SELECT * 
   FROM TEMPO A
              LEFT JOIN CLEANING_SCHEDULE B ON A.COD_DATA = B.CDATE
              LEFT JOIN LOCATION C ON C.LOC_ID = B.LOC_ID
 WHERE A.COD_DATA BETWEEN '2011-01-01' AND '2011-01-05';
*/
/*
SELECT A.FLOOR_ID, A.NUM_STANZA, A.LOC_DESC, A.DEFAULT_VAL, A.EXCEL_ROW, C.*, B.* 
   FROM LOCATION A
              LEFT JOIN (TEMPO B LEFT JOIN CLEANING_SCHEDULE C ON B.COD_DATA = C.CDATE)  ON A.LOC_ID = C.LOC_ID
WHERE A.FLOOR_ID = 'PV';

*/
SELECT B.COD_DATA, C.LOC_ID, C.CVALUE
   FROM TEMPO B
              LEFT JOIN CLEANING_SCHEDULE C ON B.COD_DATA = C.CDATE;


INSERT INTO CLEANING


SELECT B.COD_DATA, C.LOC_ID, C.CVALUE
   FROM TEMPO B
              LEFT JOIN CLEANING_SCHEDULE C ON B.COD_DATA = C.CDATE
WHERE B.COD_DATA = '2011-01-01';


/*
SELECT A.*, E.*
   FROM (LOCATION A LEFT JOIN CLEANING_SCHEDULE B ON A.LOC_ID = B.LOC_ID)
              LEFT JOIN ( SELECT C.COD_DATA, D.LOC_ID, D.CVALUE
                                     FROM TEMPO C
                                                LEFT JOIN CLEANING_SCHEDULE D ON C.COD_DATA = D.CDATE
                                          ) E ON E.LOC_ID = A.LOC_ID
  WHERE E.COD_DATA = '2011-01-01';
*/
 -- INSERT INTO CLEANING_SCHEDULE (CDATE, LOC_ID, CVALUE) VALUES ('2011-01-01', 'PV_P_01', 2.0);
 INSERT INTO CLEANING_SCHEDULE (CDATE, LOC_ID, FLOOR_ID, CVALUE) VALUES ('2015-01-06', 'A0_3', 'A0', 2.0);
-- SELECT * FROM CLEANING_SCHEDULE; 


CREATE VIEW ZZ_TEMP_CS AS (
SELECT C.COD_DATA, D.LOC_ID, D.CVALUE
                                     FROM TEMPO C
                                                LEFT JOIN CLEANING_SCHEDULE D ON C.COD_DATA = D.CDATE);