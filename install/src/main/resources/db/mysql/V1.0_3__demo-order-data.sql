
INSERT INTO `Address` (`id`, `city`, `street`, `zipCode`, `countryIso3Code`, `gender`, `firstname`, `lastname`, `company`) VALUES
(1, 'Paris', '10, rue des lilas', '75001', 'FRA', 'M.', 'Pierre', 'Durand', NULL);

INSERT INTO `Orders` (`id`, `user_id`, `transactionId`, `deliveryAddress_id`, `billingAddress_id`, `status`, `creationDate`, `updateDate`, `paymentDate`, `deliveryDate`, `parcelTrackingKey`, `price`) VALUES
(1, 1, 3, 1, 1, 'DELIVERED', '2014-06-18 00:52:52', '2014-06-18 00:52:52', '2014-06-18 00:52:52', '2014-06-18 00:52:52', 'TRACKING_KEY', 100.0),
(2, 1, 3, 1, 1, 'CREATED', '2020-06-18 00:52:52', '2020-06-18 00:52:52', NULL, '2020-12-18 00:52:52', 'TRACKING_KEY', 100.0);

INSERT INTO `OrderItem` (`id`, `order_id`, `sku_id`, `product_id`, `quantity`, `price`) VALUES
(1, 1, 1, 1, 1, 100.0),
(2, 2, 1, 1, 1, 50.0),
(3, 2, 2, 2, 2, 50.0);

INSERT INTO `OrderDiscount` (`order_id`, `discount_id`, `discountValue`) VALUES
(1, 1, 10),
(2, 2, 5.0);