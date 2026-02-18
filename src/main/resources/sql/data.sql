create type invoice_status as enum ('DRAFT', 'CONFIRMED','PAID');
create table invoice (
    id serial primary key,
    customer_name varchar not null,
    status invoice_status
);
create table invoice_line(
    id serial primary key,
    invoice_id int not null references invoice(id),
    label varchar not null,
    quantity int not null,
    unit_price numeric(10,2) not null
);
create table tax_config()

INSERT INTO invoice (customer_name, status) VALUES
                                                ('Alice', 'CONFIRMED'),
                                                ('Bob', 'PAID'),
                                                ('Charlie', 'DRAFT');
INSERT INTO invoice_line (invoice_id, label, quantity, unit_price) VALUES
                                                                       (1, 'Produit A', 2, 100),
                                                                       (1, 'Produit B', 1, 50),
                                                                       (2, 'Produit A', 5, 100),
                                                                       (2, 'Service C', 1, 200),
                                                                       (3, 'Produit B', 3, 50);
