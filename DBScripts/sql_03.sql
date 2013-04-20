SELECT distinct a.CODOSPITE,a.NOMEOSPITE,a.gmanascita,a.sesso,r.dataacc,p.* 
from laospita a
join laregole r on (a.codente=r.codente and a.codospite=r.codospite )
join clin_prenotazioni p on (a.codospite=p.codospite) -- and p.al >= :dal and p.dal <= :al 
   /*and p.stanza in (1,2,3)*/
   --)
WHERE 1=1
      --and a.codente=101
      --and p.letto in (22)
      and r.stato='P'