INSERT INTO address (id, city, street, zipcode, countryiso3code, gender, firstname, lastname, company)
VALUES (1, 'Paris', '11, Rue des lilas', '75001', 'FRA', 'M.', 'John', 'Smith', null);

INSERT INTO orders(id, user_id, transactionid, deliveryaddress_id, billingaddress_id,
status, creationdate, updatedate, paymentdate, deliverydate, parceltrackingkey, price) VALUES
(1, 1, 1, 1, 1, 'PAYMENT_VALIDATED', '2020-11-26 00:52:52', '2020-11-26 00:52:52', null, null, null, 234.5),
(2, 1, 2, 1, 1, 'DELIVERED', '2020-11-26 00:52:52', '2020-11-26 00:52:52', '2020-11-26 01:52:52', '2020-11-28 08:52:52', 'EKJKJKKH7676', 99.9),
(3, 1, 3, 1, 1, 'CREATED', '2020-06-18 00:52:52', '2020-06-18 00:52:52', NULL, '2020-12-18 00:52:52', null, 100.0);

INSERT INTO orderitem(order_id, sku_id, product_id, quantity, price) VALUES
(1, 1, 1, 1, 200.0),
(1, 3, 2, 1, 34.5),
(2, 5, 3, 1, 99.0),
(3, 2, 1, 1, 99.0);

INSERT INTO orderdiscount(order_id, discount_id, discountvalue)
VALUES (1, 1, 10.0);