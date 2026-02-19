/**Q1**/
SELECT i.id,
       i.customer_name,
       i.status,
       SUM(il.quantity * il.unit_price) AS total
FROM invoice i
         JOIN invoice_line il ON i.id = il.invoice_id
GROUP BY i.id, i.customer_name, i.status;

/**Q2**/
SELECT i.id,
       i.customer_name,
       i.status,
       SUM(il.quantity * il.unit_price) AS total
FROM invoice i
         JOIN invoice_line il ON i.id = il.invoice_id
WHERE i.status IN ('CONFIRMED','PAID')
GROUP BY i.id, i.customer_name, i.status;

/**Q3**/
SELECT
    SUM(CASE WHEN i.status='PAID'
                 THEN il.quantity*il.unit_price ELSE 0 END) AS total_paid,

    SUM(CASE WHEN i.status='CONFIRMED'
                 THEN il.quantity*il.unit_price ELSE 0 END) AS total_confirmed,

    SUM(CASE WHEN i.status='DRAFT'
                 THEN il.quantity*il.unit_price ELSE 0 END) AS total_draft
FROM invoice i
         JOIN invoice_line il ON i.id = il.invoice_id;

/**Q4**/
SELECT SUM(
               CASE
                   WHEN i.status = 'PAID'
                       THEN il.quantity * il.unit_price
                   WHEN i.status = 'CONFIRMED'
                       THEN il.quantity * il.unit_price * 0.5
                   ELSE 0
                   END
       ) AS weighted_turnover
FROM invoice i
         JOIN invoice_line il ON i.id = il.invoice_id;

/**Q5A**/
SELECT
    i.id,
    SUM(il.quantity * il.unit_price) AS total_ht,
    SUM(il.quantity * il.unit_price) * tc.rate / 100 AS total_tva,
    SUM(il.quantity * il.unit_price) * (1 + tc.rate / 100) AS total_ttc
FROM invoice i
         JOIN invoice_line il ON i.id = il.invoice_id
         CROSS JOIN tax_config tc
GROUP BY i.id, tc.rate
ORDER BY i.id;

/**Q5B**/
SELECT SUM(
               CASE
                   WHEN i.status='PAID'
                       THEN il.quantity * il.unit_price * (1 + tc.rate/100)
                   WHEN i.status='CONFIRMED'
                       THEN il.quantity * il.unit_price * 0.5 * (1 + tc.rate/100)
                   ELSE 0
                   END
       ) AS weighted_ttc
FROM invoice i
         JOIN invoice_line il ON i.id = il.invoice_id
         CROSS JOIN tax_config tc;
