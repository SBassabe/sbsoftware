----------------------------------------------------------------------
-- Grafica Regina
-- Creazione di database H2 con file name 'dbfile'
-- sotto jdbc:h2:${catalina.home}/conf/reginadb/dbfile
-- History
--    15:56 29/12/2014 - maiden create
----------------------------------------------------------------------

DROP TABLE DOCDTMAP_2;
DROP TABLE CLEANING_SCHEDULE;
DROP TABLE LOCATION;
DROP TABLE TEMPO;

CREATE TABLE DOCDTMAP_2 (
   LOC_ID VARCHAR(10),
   GMADAL DATE, 
   GMAAL DATE, 
   DOCID INT, 
   DOCNAME VARCHAR(255)
);

CREATE TABLE CLEANING_SCHEDULE (
	CDATE DATE,
	FLOOR_ID VARCHAR(10),
	LOC_ID VARCHAR(10),
	CVALUE DECIMAL(20,2)
);

CREATE TABLE LOCATION (
	LOC_ID VARCHAR(10),   -- eg. A0_24
	FLOOR_ID VARCHAR(10),
	COD_STAN VARCHAR(5),
	NUM_STANZA VARCHAR(10),
	LOC_DESC VARCHAR(50),
	OCC_DEPENDENT VARCHAR(1),
	DEFAULT_VAL DECIMAL(20,2),
	EXCEL_ROW INT
);

CREATE TABLE TEMPO (
	COD_DATA DATE,
	DESC_MONTH VARCHAR(30),
	DESC_YEAR VARCHAR(4)
);

