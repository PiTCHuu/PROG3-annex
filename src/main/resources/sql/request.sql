SELECT i.id,
       i.customer_name,
       i.status,
       SUM(il.quantity * il.unit_price) AS total
FROM invoice i
         JOIN invoice_line il ON i.id = il.invoice_id
GROUP BY i.id, i.customer_name, i.status;

SELECT i.id,
       i.customer_name,
       i.status,
       SUM(il.quantity * il.unit_price) AS total
FROM invoice i
         JOIN invoice_line il ON i.id = il.invoice_id
WHERE i.status IN ('CONFIRMED','PAID')
GROUP BY i.id, i.customer_name, i.status;

SELECT
    SUM(CASE WHEN i.status='PAID'
                 THEN il.quantity*il.unit_price ELSE 0 END) AS total_paid,

    SUM(CASE WHEN i.status='CONFIRMED'
                 THEN il.quantity*il.unit_price ELSE 0 END) AS total_confirmed,

    SUM(CASE WHEN i.status='DRAFT'
                 THEN il.quantity*il.unit_price ELSE 0 END) AS total_draft
FROM invoice i
         JOIN invoice_line il ON i.id = il.invoice_id;
