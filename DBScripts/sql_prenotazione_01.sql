SELECT a.sesso,d.stanza,d.codice_letto,a.gmaing,a.gmadim,d.gmainizioutili,d.gmafineutili,a.codospite
  FROM ospiti_a a 
  JOIN ospiti_d d on (a.codospite=d.codospite) 
  WHERE a.gmaing<=CAST('2012-07-18' AS DATE) 
  AND (a.gmadim is null or (a.gmadim>CAST('2012-07-18' AS DATE))) 
  AND d.gmainizioutili<=CAST('2012-07-18' AS DATE) 
  AND ((d.gmafineutili>CAST('2012-07-18' AS DATE)) or d.gmafineutili is null)
  AND d.codice_letto in (21,22) -- Array di letti associati ad un specifico piano.

/*
In realtà le prenotazioni funzionano in modo differente .. 

- nella tabella ospiti_a trovi tutte le anagrafiche dei ricoverati, mentre le anagrafiche in lista attesa o prenotate le trovi sulla tabella LaOspita.
- lo stato della pratica, ovvero lista attesa-prenotato-passato in struttura o rinuncia ecc lo si trova nella tabella LaRegole
- mentre il letto prenotato è sulla tabella ClinPrenotazioni..

Nel momento che accetto una peersona in struttura, vengono copiati i dati anagrafici nella ospiti_a , la tabella laregole viene impostata come passato in struttura (stato="X") e la prenotazinoe viene spostata (cancella da clinprenotazioni e metto su ospiti_d...). 
/*Quanto sopra è per farti capire il funzionamento, la query che serve a te per capire se la stanza è occupata ad una certa data (o in un intervallo di date) è la stassa che uso nel tabellone ed è la seguente
/*
SELECT distinct a.CODOSPITE,a.NOMEOSPITE,a.gmanascita,a.sesso,r.dataacc,p.* from laospita a
join laregole r on (
   a.codente=r.codente and a.codospite=r.codospite )
join clin_prenotazioni p on (
   a.codospite=p.codospite and p.al >= :dal and p.dal <= :al 
   and p.stanza in (1,2,3)
   )
WHERE a.codente=101
and r.stato='P'

Nel caso in cui sul tabellone impostano dei filtri per visualizzare un reparto in particolare passo alla query la condizione di filtro "and p.stanza in (1,2,3)" dove 1,2,3 sono il codice progressivo della gestanze.. 

/*
Se qualcosa non ti è chiaro scrivimi oppure chiamami..


Ciao

*/

SELECT distinct a.CODOSPITE,a.NOMEOSPITE,a.gmanascita,a.sesso,r.dataacc,p.* 
  from laospita a
 join laregole r on (a.codente=r.codente and a.codospite=r.codospite and r.stato='P' )
 join clin_prenotazioni p on (a.codospite=p.codospite)-- and p.al >= :dal and p.dal <= :al /*and p.stanza in (1,2,3)*/)
WHERE 1=1
    --AND a.codente=101
--and r.stato='P'  

SELECT DISTINCT a.codente
  from laospita a

-- SELECT a.sesso,d.stanza,d.codice_letto,a.gmadim,d.gmainizioutili,d.gmafineutili,a.codospite,a.nomeospite,d.sede,d.reparto
SELECT distinct a.sesso,p.stanza,p.letto codice_letto,p.dal,p.al,a.CODOSPITE,a.NOMEOSPITE,p.sede,p.reparto 
  from laospita a
 join laregole r on (a.codente=r.codente and a.codospite=r.codospite and r.stato='P')
 join clin_prenotazioni p on (a.codospite=p.codospite and p.al >= CAST('2012-07-18' AS DATE) and p.dal <= CAST('2012-07-18' AS DATE) and p.letto IN (22, 21))
 
 WHERE 1=1
    and r.stato='P'

    SELECT distinct a.sesso,p.stanza,p.letto codice_letto,p.dal,p.al,a.CODOSPITE,a.NOMEOSPITE,p.sede,p.reparto 
    from laospita a  join laregole r on (a.codente=r.codente and a.codospite=r.codospite and r.stato='P')  
    join clin_prenotazioni p on (a.codospite=p.codospite  and p.al >= CAST('2012-07-18' AS DATE) and p.dal <= CAST('2012-07-18' AS DATE) and p.letto IN (186, 188, 70, 71, 196, 72, 73, 190, 74, 75, 76)) 