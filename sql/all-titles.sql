select
   b.book_title as "Title", 
   m.med_desc as "Media",
   group_concat(a.auth_name) as "Authors"
from
   Book b
join
   Media_Type m on m.med_id=b.med_id
join
   Author_Book_Xref x on x.book_id=b.book_id
join
   Author a  on a.auth_id=x.auth_id
group by
   b.book_title, m.med_desc
order by
   1