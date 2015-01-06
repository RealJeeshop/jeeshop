SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: apps
--


--
-- Dumping data for table Media
--

INSERT INTO Media (id, uri) VALUES
(1, 'remi.png'),
(2, 'remi.png'),
(3, 'remi.png'),
(5, 'vero.png'),
(6, 'remi.png'),
(7, 'vero.png'),
(8, 'vtt_small.jpg'),
(9, 'vtt_large.jpg'),
(10, 'vtt_small.jpg'),
(11, 'vtt_thumbnail.jpg'),
(12, 'vtt_large.jpg'),
(13, 'vtt_small.jpg'),
(14, 'vtt_thumbnail.jpg'),
(15, 'vtt_thumbnail.jpg'),
(16, 'IMGP2376.JPG'),
(17, 'vtt_large.jpg'),
(18, 'vtt_small.jpg'),
(19, 'vtt_large.jpg'),
(20, 'vtt_small.jpg'),
(21, 'vtt_small.jpg'),
(22, 'vtt_large.jpg'),
(23, 'vtt_small.jpg'),
(24, 'vtt_thumbnail.jpg');


--
-- Dumping data for table Presentation
--

INSERT INTO Presentation (id, longDescription, displayName, features, locale, shortDescription, largeImage_id, smallImage_id, thumbnail_id, video_id) VALUES
(4, 'test', 'Test', NULL, 'fr_FR', 'test1', NULL, NULL, NULL, NULL),
(6, 'Ce nouveau VTT de la gamme Energy vous procure des sensations de roulement incomparables en ville comme à la montagne.', 'VTT Energy 2', NULL, 'fr_FR', 'Le nouveau VTT de la gamme Energy de Bikester', 17, 18, 15, NULL),
(7, 'This new mountain bike gives you amazing sensations on city streets, or mountain trails.', 'Energy 2 mountain bike', NULL, 'en_US', 'The new Energy mountain bike produced by Bikester', 22, 23, 24, NULL);


--
-- Dumping data for table Catalog
--

INSERT INTO Catalog (id, description, disabled, endDate, name, startDate) VALUES
(1, 'Catalog of Hyperbike store', b'0', NULL, 'Hyperbike catalog', NULL);

--
-- Dumping data for table Product
--

INSERT INTO Product (id, description, disabled, endDate, name, startDate) VALUES
(1, 'VTT energy X1', b'0', NULL, 'Energy X1', NULL),
(2, 'VTT Energy X2', b'0', '2014-06-17 00:52:52', 'Energy X2', '2014-06-18 00:52:52'),
(3, 'VTC Lazer V2', b'1', NULL, 'Lazer V2', NULL),
(4, 'VTC Beam 3000', b'0', '2014-06-17 00:52:52', 'Beam 3000', '2014-06-18 00:52:52'),
(5, 'Michelin NX01 tire', b'0', NULL, 'Michelin NX01', NULL);


--
-- Dumping data for table Category
--

INSERT INTO Category (id, description, disabled, endDate, name, startDate) VALUES
(1, 'Bikes main category', b'0', NULL, 'Bikes', NULL),
(2, 'Accessories main category', b'0', NULL, 'Accessories', NULL),
(3, 'Tires rubric', b'0', NULL, 'Tires', NULL),
(4, 'Brackets rubric', b'0', NULL, 'Brackets', NULL),
(5, 'Handlebar rubric', b'0', NULL, 'Handlebar', NULL),
(6, 'Wheels rubric', b'0', '2014-06-19 00:52:52', 'Wheels', '2014-06-18 00:52:52'),
(7, 'VTT rubric', b'0', NULL, 'VTT', NULL),
(12, 'VTC rubric', b'0', NULL, 'VTC', NULL),
(13, 'Bikes selection for christmas', b'0', '2014-12-30 18:00:00', 'Christmas bikes', '2014-12-14 18:00:00');

--
-- Dumping data for table Category_Category
--

INSERT INTO Category_Category (parentCategoryId, childCategoryId, orderIdx) VALUES
(2, 3, 0),
(2, 4, 1),
(2, 5, 2),
(2, 6, 3),
(2, 7, 4);

--
-- Dumping data for table Catalog_Category
--

INSERT INTO Catalog_Category (catalogId, categoryId, orderIdx) VALUES
(1, 1, 0),
(1, 2, 1);


--
-- Dumping data for table Category_Product
--

INSERT INTO Category_Product (categoryId, productId, orderIdx) VALUES
(7, 1, 0),
(7, 2, 1),
(12, 3, 0),
(12, 4, 1),
(3, 5, 0);

--
-- Dumping data for table Discount
--

INSERT INTO Discount (id, description, disabled, endDate, name, startDate, discountValue, triggerValue, triggerRule, type, uniqueUse, usesPerCustomer, voucherCode) VALUES
(1, 'a discount', b'0', '2014-06-19 00:52:52', 'discount1', '2014-06-18 00:52:52', 0.1, NULL, 'AMOUNT', 'DISCOUNT_RATE', b'1', 1, NULL);


--
-- Dumping data for table Product_Presentation
--

INSERT INTO Product_Presentation (catalogItemId, presentationId) VALUES
(2, 6),
(2, 7);

--
-- Dumping data for table SKU
--

INSERT INTO SKU (id, description, disabled, endDate, name, startDate, currency, price, quantity, reference, threshold) VALUES
(1, 'Energy X1 batch of 10', b'0', '2014-10-01 00:52:52', 'Energy X1 batch of 10', '2014-06-18 00:52:52', 'EUR', 10, 100, 'X1213JJLB-1', 3),
(2, 'Energy X1 batch of 50', b'1', '2015-01-01 00:52:52', 'Energy X1 batch of 50', '2014-10-02 00:52:52', NULL, 10, 100, 'X1213JJLB-2', 3),
(3, 'Energy X2', b'0', NULL, 'Energy X2', NULL, NULL, 10, 100, 'X1213JJLB-3', 3),
(4, 'Michelin NX01', b'0', NULL, 'Michelin NX01', NULL, NULL, 10, 2, 'X1213JJLB-4', 3),
(5, 'Lazer V2', b'0', NULL, 'Lazer V2', NULL, NULL, 10, 100, 'X1213JJLB-5', 3),
(6, 'Beam 3000', b'0', NULL, 'Beam 3000', NULL, NULL, NULL, NULL, NULL, NULL);

--
-- Dumping data for table Product_SKU
--

INSERT INTO Product_SKU (productId, skuId, orderIdx) VALUES
(1, 1, 0),
(1, 2, 1),
(2, 3, 0),
(5, 4, 0),
(4, 6, 0);


--
-- Dumping data for table SKU_Discount
--

INSERT INTO SKU_Discount (skuId, discountId, orderIdx) VALUES
(5, 1, 0);



/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;