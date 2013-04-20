Select a.codospite, a.nomeospite, a.sesso, a.gmaing, 
       --m.gmadal, m.gmaal, 
       a.gmadim, d.gmainizioutili, d.gmafineutili, d.sede, d.reparto, d.stanza,d.codice_letto
       --,t.descr 
from ospiti_a a
join ospiti_d d on (a.codospite=d.codospite)
where 1=1
--and a.nomeospite LIKE 'CAIA%LUC%'
and d.codice_letto IN (23,24)
--left join clin_medico_stanza m on (m.codstan=d.stanza and m.gmadal<=:data and (m.gmaal is null or (m.gmaal>=:data)))
--left join teanapers t on (t.progr=m.progmedico)
--where a.gmaing<=:data
--and (a.gmadim is null or (a.gmadim>:data))
--and d.gmainizioutili<=:data
--and ((d.gmafineutili>:data) or d.gmafineutili is null)
order by 4

SELECT a.sesso,d.stanza,d.codice_letto,a.gmaing,a.gmadim,m.gmadal,m.gmaal,d.gmainizioutili,d.gmafineutili,a.codospite,a.nomeospite,d.sede,d.reparto 
FROM ospiti_a a 
JOIN ospiti_d d on (a.codospite=d.codospite) 
LEFT JOIN clin_medico_stanza m on (m.codstan=d.stanza and m.gmadal<= CAST('2012-06-28' AS DATE)  and (m.gmaal is null or (m.gmaal >= CAST('2012-06-28' AS DATE)))) 
WHERE a.gmaing<=CAST('2012-06-28' AS DATE) 
      AND (a.gmadim is null or (a.gmadim>CAST('2012-06-28' AS DATE))) 
      AND d.gmainizioutili<=CAST('2012-06-28' AS DATE) 
      AND ((d.gmafineutili>CAST('2012-06-28' AS DATE)) or d.gmafineutili is null) 
      AND	d.codice_letto IN (35, 36, 39, 37, 192, 42, 40, 67, 66, 69, 68, 22, 27, 6, 4, 31, 8, 55, 11, 12, 64, 65, 62, 63, 60, 61, 49, 45, 44, 46, 10, 54)

SELECT a.sesso,d.stanza,d.codice_letto,a.gmaing,a.gmadim,m.gmadal,m.gmaal,d.gmainizioutili,d.gmafineutili,d.fineconfermata,a.codospite,a.nomeospite,d.sede,d.reparto 
FROM ospiti_a a 
JOIN ospiti_d d on (a.codospite=d.codospite) 
LEFT JOIN clin_medico_stanza m on (m.codstan=d.stanza) -- and m.gmadal<= CAST('2012-06-28' AS DATE) 
--and (m.gmaal is null or (m.gmaal >= CAST('2012-06-28' AS DATE)))) 
WHERE 1=1 
-- AND a.gmaing<=CAST('2012-06-28' AS DATE) AND (a.gmadim is null or (a.gmadim>CAST('2012-06-28' AS DATE))) 
-- AND d.gmainizioutili<=CAST('2012-06-28' AS DATE) 
-- AND ((d.gmafineutili>CAST('2012-06-28' AS DATE)) or d.gmafineutili is null)
--and d.codice_letto IN (25,26)
--and d.stanza in (662)
and (a.nomeospite like '%BASANISI ANDREA%' OR a.nomeospite like '%MARTUCCI%')
ORDER BY nomeospite

and d.codice_letto IN (35, 36, 33, 34, 39, 37, 192, 38, 43, 42, 41, 40, 67, 202, 66, 69, 68, 22, 23, 24, 25, 26, 27, 28, 29, 3, 2, 1, 7, 30, 6, 5, 32, 4, 31, 9, 8, 59, 58, 57, 56, 19, 55, 17, 18, 15, 16, 13, 14, 11, 12, 21, 20, 64, 65, 62, 63, 60, 61, 49, 48, 45, 44, 47, 46, 10, 51, 52, 53, 54, 50)

SELECT a.sesso,d.stanza,d.codice_letto,a.gmadim,d.gmainizioutili,d.gmafineutili,a.codospite,a.nomeospite,d.fineconfermata,d.sede,d.reparto 
FROM ospiti_a a JOIN ospiti_d d on (a.codospite=d.codospite) LEFT JOIN clin_medico_stanza m on (m.codstan=d.stanza and m.gmadal<= CAST('2012-07-03' AS DATE) and (m.gmaal is null or (m.gmaal >= CAST('2012-07-03' AS DATE)))) WHERE a.gmaing<=CAST('2012-07-03' AS DATE) AND (a.gmadim is null or (a.gmadim>CAST('2012-07-03' AS DATE))) AND d.gmainizioutili<=CAST('2012-07-03' AS DATE) AND ((d.gmafineutili>CAST('2012-07-03' AS DATE)) or d.gmafineutili is null) and 
d.codice_letto IN (35, 36, 33, 34, 39, 37, 192, 38, 43, 42, 41, 40, 67, 202, 66, 69, 68, 22, 23, 24, 25, 26, 27, 28, 29, 3, 2, 1, 7, 30, 6, 5, 32, 4, 31, 9, 8, 59, 58, 57, 56, 19, 55, 17, 18, 15, 16, 13, 14, 11, 12, 21, 20, 64, 65, 62, 63, 60, 61, 49, 48, 45, 44, 47, 46, 10, 51, 52, 53, 54, 50)      

SELECT a.sesso,d.stanza,d.codice_letto,a.gmaing,a.gmadim,m.gmadal,m.gmaal,d.gmainizioutili,d.gmafineutili, d.gmainizioutili-d.gmafineutili, d.fineconfermata, a.codospite,a.nomeospite,d.sede,d.reparto 
FROM ospiti_a a JOIN ospiti_d d on (a.codospite=d.codospite) 
LEFT JOIN clin_medico_stanza m on (m.codstan=d.stanza and m.gmadal<= CAST('2012-07-01' AS DATE) and (m.gmaal is null or (m.gmaal >= CAST('2012-07-01' AS DATE)))) 
WHERE 1=1 
-- and d.fineconfermata = 'T' 
AND a.gmaing<=CAST('2012-07-01' AS DATE) AND (a.gmadim is null or (a.gmadim>CAST('2012-07-01' AS DATE))) AND d.gmainizioutili<=CAST('2012-07-01' AS DATE) AND ((d.gmafineutili>CAST('2012-07-01' AS DATE)) or d.gmafineutili is null) and d.codice_letto IN (35, 36, 33, 34, 39, 37, 192, 38, 43, 42, 41, 40, 67, 202, 66, 69, 68, 22, 23, 24, 25, 26, 27, 28, 29, 3, 2, 1, 7, 30, 6, 5, 32, 4, 31, 9, 8, 59, 58, 57, 56, 19, 55, 17, 18, 15, 16, 13, 14, 11, 12, 21, 20, 64, 65, 62, 63, 60, 61, 49, 48, 45, 44, 47, 46, 10, 51, 52, 53, 54, 50)
