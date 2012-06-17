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