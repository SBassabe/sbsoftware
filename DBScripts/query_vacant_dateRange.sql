-------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- Brainstorm
-- Query needed to get vacant room range given the room number and known vacant date
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------


SELECT d.codice_letto, max(d.GMAFINEUTILI) 
  FROM ospiti_a a JOIN ospiti_d d on (a.codospite=d.codospite) 
       LEFT JOIN clin_medico_stanza m on m.codstan=d.stanza --and (m.gmadal<= CAST('2014-12-31' AS DATE) and (m.gmaal is null or (m.gmaal >= CAST('2014-12-01' AS DATE)))) 
 WHERE 1=1
       AND d.gmafineutili < CAST('2014-12-20' AS DATE)
       AND d.codice_letto IN (46)
       --AND d.STANZA = 685
group by d.codice_letto
union
SELECT d.codice_letto, min(d.GMAFINEUTILI) 
  FROM ospiti_a a JOIN ospiti_d d on (a.codospite=d.codospite) 
       LEFT JOIN clin_medico_stanza m on m.codstan=d.stanza --and (m.gmadal<= CAST('2014-12-31' AS DATE) and (m.gmaal is null or (m.gmaal >= CAST('2014-12-01' AS DATE)))) 
 WHERE 1=1
       AND d.gmafineutili > CAST('2014-12-20' AS DATE)
       AND d.codice_letto IN (46)
       --AND d.STANZA = 685
group by d.codice_letto;          



-- This one finds the start range       
SELECT distinct d.codice_letto, max(d.GMAFINEUTILI) 
  FROM ospiti_d d 
 WHERE 1=1
       AND d.gmafineutili <= CAST('2014-09-20' AS DATE)
       --AND d.codice_letto IN (143, 188, 196, 190, 186, 70, 71, 72, 73, 74, 75, 76)
       AND d.CODICE_LETTO = 143
       --AND d.STANZA = 685
 group by d.CODICE_LETTO;         
 
 -- This one finds the End Range
 SELECT distinct d.codice_letto, min(d.GMAINIZIOUTILI) 
  FROM ospiti_d d 
 WHERE 1=1
       AND d.GMAINIZIOUTILI >= CAST('2014-09-20' AS DATE)
       --AND d.codice_letto IN (143, 188, 196, 190, 186, 70, 71, 72, 73, 74, 75, 76)
       AND d.CODICE_LETTO = 143
       --AND d.STANZA = 685
 group by d.CODICE_LETTO;           
              