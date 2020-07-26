select
   b.book_title as "Title", m.med_desc as "Media"
From
   Book b
join
   Media_Type m on m.med_id=b.med_id
order by
   1