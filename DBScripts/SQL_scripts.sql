Select a.codospite,a.nomeospite,a.sesso, a.gmaing from ospiti_a a

SELECT * 

--Query elenco ospiti presenti ad una certa data con stanza occupata e medico (da decodificare !!):
Select a.codospite,a.nomeospite,a.sesso,d.sede,d.reparto,d.stanza,d.codice_letto,t.descr 
from ospiti_a a
join ospiti_d d on (
  a.codospite=d.codospite)
left join clin_medico_stanza m on (
  m.codstan=d.stanza
  and m.gmadal<=:data
  and (m.gmaal is null or (m.gmaal>=:data))
  )
left join teanapers t on (
  t.progr=m.progmedico)
where a.gmaing<=:data
and (a.gmadim is null or (a.gmadim>:data))
and d.gmainizioutili<=:data
and ((d.gmafineutili>:data) or d.gmafineutili is null)

-- View Created
RECREATE VIEW REGINA_LOGISTICA_V AS
SELECT a.codospite,a.nomeospite,a.sesso,d.sede,d.reparto,d.stanza,d.codice_letto,t.descr 
 from ospiti_a a
 join ospiti_d d on (a.codospite=d.codospite)
 left join clin_medico_stanza m on (m.codstan=d.stanza)
left join teanapers t on (t.progr=m.progmedico)

where a.gmadim is null 
and d.gmafineutili is null;

SELECT * FROM REGINA_LOGISTICA_V;