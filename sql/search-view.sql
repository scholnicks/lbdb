drop view v_search;

create view v_search as
    select
        b.*, a.auth_id, a.auth_name, x.abx_editor
    from
        book b
    join
		author_book_xref x on b.book_id=x.book_id
    join
        author a on a.auth_id=x.auth_id
;
