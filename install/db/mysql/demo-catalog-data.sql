-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jan 04, 2016 at 01:58 AM
-- Server version: 5.5.46-0ubuntu0.14.04.2
-- PHP Version: 5.5.9-1ubuntu4.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `jeeshop`
--

--
-- Dumping data for table `Catalog`
--

INSERT INTO `Catalog` (`id`, `description`, `disabled`, `endDate`, `name`, `startDate`) VALUES
(1, 'Catalog of Hyperbike store', b'0', NULL, 'Hyperbike catalog', NULL);

--
-- Dumping data for table `Catalog_Category`
--

INSERT INTO `Catalog_Category` (`catalogId`, `categoryId`, `orderIdx`) VALUES
(1, 1, 0),
(1, 2, 1);

--
-- Dumping data for table `Category`
--

INSERT INTO `Category` (`id`, `description`, `disabled`, `endDate`, `name`, `startDate`) VALUES
(1, 'Bikes main category', b'0', NULL, 'Bikes', NULL),
(2, 'Accessories main category', b'0', NULL, 'Accessories', NULL),
(3, 'Tires rubric', b'0', NULL, 'Tires', NULL),
(4, 'Brackets rubric', b'0', NULL, 'Brackets', NULL),
(5, 'Handlebar rubric', b'0', NULL, 'Handlebar', NULL),
(6, 'Wheels rubric', b'0', '2014-06-19 00:52:52', 'Wheels', '2014-06-18 00:52:52'),
(7, 'Mountain Bikes category', b'0', NULL, 'Mountain Bikes', NULL),
(12, 'Racing Bikes category', b'0', NULL, 'Racing Bikes', NULL);

--
-- Dumping data for table `Category_Category`
--

INSERT INTO `Category_Category` (`parentCategoryId`, `childCategoryId`, `orderIdx`) VALUES
(2, 3, 0),
(2, 4, 1),
(2, 5, 2),
(2, 6, 3),
(1, 7, 0),
(1, 12, 1);

--
-- Dumping data for table `Category_Presentation`
--

INSERT INTO `Category_Presentation` (`catalogItemId`, `presentationId`) VALUES
(1, 13),
(1, 14),
(2, 15),
(2, 16),
(7, 17),
(12, 19),
(12, 20),
(7, 21);

--
-- Dumping data for table `Category_Product`
--

INSERT INTO `Category_Product` (`categoryId`, `productId`, `orderIdx`) VALUES
(7, 1, 0),
(7, 2, 1),
(12, 3, 0),
(12, 4, 1),
(3, 5, 0);

--
-- Dumping data for table `Discount`
--

INSERT INTO `Discount` (`id`, `description`, `disabled`, `endDate`, `name`, `startDate`, `discountValue`, `triggerValue`, `triggerRule`, `type`, `applicableTo`, `uniqueUse`, `usesPerCustomer`, `voucherCode`) VALUES
(1, 'a discount', b'0', '2014-06-19 00:52:52', 'discount1', '2014-06-18 00:52:52', 0.1, NULL, 'AMOUNT', 'DISCOUNT_RATE', '', b'1', 1, NULL);

--
-- Dumping data for table `Media`
--

INSERT INTO `Media` (`id`, `uri`) VALUES
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
(16, 'IMGP2376.JPG'),
(19, 'vtt_large.jpg'),
(20, 'vtt_small.jpg'),
(21, 'vtt_small.jpg'),
(25, 'bikes_root_cat.jpg'),
(26, 'bikes_root_cat.jpg'),
(27, 'bikes_root_cat.jpg'),
(28, 'bikes_root_cat.jpg'),
(29, 'accessories_root_cat.jpg'),
(30, 'accessories_root_cat.jpg'),
(31, 'bikes_root_cat.jpg'),
(33, 'racing-bikes.jpg'),
(34, 'racing-bikes.jpg'),
(35, 'bikes_root_cat.jpg'),
(36, 'energy_x1.jpg'),
(37, 'energy_x1.jpg'),
(38, 'energy_x2.jpg'),
(39, 'energy_x2.jpg');

--
-- Dumping data for table `Presentation`
--

INSERT INTO `Presentation` (`id`, `displayName`, `promotion`, `features`, `locale`, `shortDescription`, `mediumDescription`, `longDescription`, `largeImage_id`, `smallImage_id`, `thumbnail_id`, `video_id`) VALUES
(4, 'Test', NULL, NULL, 'fr_FR', 'test1', NULL, 'test', NULL, NULL, NULL, NULL),
(13, 'Vélos', 'Notre sélection de vélos', NULL, 'fr', NULL, NULL, NULL, 28, NULL, NULL, NULL),
(14, 'Bikes', 'Our bikes selection', NULL, 'en', NULL, NULL, NULL, 27, NULL, NULL, NULL),
(15, 'Accessories', 'Our accessory kits', NULL, 'en', NULL, NULL, NULL, 29, NULL, NULL, NULL),
(16, 'Accessoires', 'Nos kits d''accessoires', NULL, 'fr', NULL, NULL, NULL, 30, NULL, NULL, NULL),
(17, 'Mountain Bikes', 'Mountain Bikes', NULL, 'en', 'Looking for a MTB that is high-performance and comfortable? We introduce the Engergy all-suspension MTB line.', 'These MTBs have specially-designed geometries for high-performance and competition riding, with high-end components for increased adjustment options, accuracy and comfort.', NULL, 31, NULL, NULL, NULL),
(19, 'Racing Bikes', 'Racing Bikes', NULL, 'en', 'For lovers of speed, road riding or just bike strolls: our road bikes are designed both for cyclists who want to learn how to ride on the road and for demanding competitors.', 'You will inevitably find what you like in the sports range of bikes for beginners, sports road bikes for regular practitioners and road racing bikes for amateurs seeking speed and eager to pit themselves against any mountain pass.', NULL, 33, NULL, NULL, NULL),
(20, 'Vélos route', 'Vélos route', NULL, 'fr', 'Pour les amateurs de vitesse, de la route ou simplement de balade : nos vélos course sont conçus aussi bien pour les cyclistes qui souhaitent s''initier à la pratique du vélo route tout comme les compétiteurs exigeants.', 'Vous trouverez forcément votre bonheur entre les vélos route de la gamme Sport pour ceux qui débutent, les vélos route sport destinés à des pratiquants réguliers mais encore, les vélos route compétition taillés pour des amateurs en recherche de vitesse et qui souhaitent s''attaquer à n''importe quel col.', NULL, 34, NULL, NULL, NULL),
(21, 'Vélos tout terrain', 'Vélos tout terrain', NULL, 'fr', 'Son terrain de jeu est la montagne, les chemins escarpés, les single track exigeants.', '<p>L''objectif de la pratique est de grimper et descendre partout, aussi bien pour se dépenser que pour l''adrénaline des descentes.</p>\n<p>Les VTT All Mountain se caractérisent par des géométries particulières, ils sont tout suspendus (avant - arrière) équipés de systèmes de réglages aussi sophistiqués en conception que simples en utilisation et de solutions anti-pompage (phénomène de déperdition de la puissance au pédalage) qui assurent la rigidité face aux efforts de pédalage et la fluidité face au relief du terrain.</p>', NULL, 35, NULL, NULL, NULL),
(22, 'Energy X1', 'Energy X1', NULL, 'en', 'The N° 1 racing and endurance mountain bike: A comfortable and responsive, all-terrain bike, with its double disc brakes.', '<dl>\n<dt>User comfort</dt>\n<dd>Raised riding position, for efficient, painless cycling. Lamellar grips.</dd>\n<dt>Efficiency</dt>\n<dd>Aluminium frame: lighter and higher performance. 160 mm double disc brakes.\n<dt>Easy adjustment</dt>\n<dd>INNOVATION: adjust the fork to your weight in less than 1 minute\n<dt>Lifetime warranty</dt>\n<dd>We guarantee the frame, stem and handlebar for life.\n<dt>Precision</dt>\nDisc brakes provide powerful braking in all weather conditions. Trigger shifter.\n</dl>', NULL, 36, NULL, NULL, NULL),
(23, 'Energy X1', 'Energy X1', NULL, 'fr', 'Le 1er VTT de randonnée sportive : un vélo tout terrain confortable et efficace, grâce à ses doubles freins à disque.', '<dl>\n<dt>Confort d''accueil</dt>\n<dd>Position relevée, pour rouler efficacement sans douleur. Poignées à lamelles.</dd>\n<dt>Efficacité</dt>\n<dd>Cadre aluminium : plus de lègèreté de rendement. Double freins à disque 160mm.</dd>\n<dt>Facilité de réglage</dt>\n<dd>INNOVATION: sélectionnez votre poids pour régler votre fourche en moins d''1 min</dd>\n<dt>Garantie à vie</dt>\n<dd>Nous garantissons à vie le cadre, la potence et le cintre.</dd>\n<dt>Précision</dt>\n<dd>Freins à disque puissants dans toutes les conditions météo. Vitesses à manette. </dd></dl>', NULL, 37, NULL, NULL, NULL),
(24, 'Energy X2', 'Energy X2', NULL, 'en', 'The first genuine touring mountain bike: comfortable thanks to a more upright seating position and front suspension, efficient thanks to a lightweight aluminium frame and 21 speeds.', NULL, NULL, 38, NULL, NULL, NULL),
(25, 'Energy X2', 'Energy X2', NULL, 'fr', 'Le 1er vrai VTT de rando : confortable par sa position relevée et sa suspension avant, et efficace grâce à son cadre aluminium léger et ses 21 vitesses.', NULL, NULL, 39, NULL, NULL, NULL),
(26, 'XL', '', NULL, 'en', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(27, 'L', '', NULL, 'en', NULL, NULL, NULL, NULL, NULL, NULL, NULL);

--
-- Dumping data for table `Presentation_Feature`
--

INSERT INTO `Presentation_Feature` (`presentationId`, `name`, `value`) VALUES
(22, 'Brakes', 'Aluminium brake levers: lightweight and precise.'),
(22, 'Frame', 'SPORT MTB frame, in strengthened, 6061 aluminium. Sports geometry, perfect for intensive mountain biking.'),
(22, 'Specifications', '14.07 kg without pedals<br/>Suspension fork<br/>26 inches<br/>M: 165-175, L: 175-180<br/>Black / Orange / White'),
(22, 'Suspension', 'No rear suspension.'),
(22, 'Tyres', '26 x 2.00 Dry tyres, with a versatile profile for excellent performance and great lateral traction.'),
(22, 'Weight', '14.07 kg without pedals.'),
(23, 'Cadre', 'Cadre VTT SPORT, en aluminium renforcé 6061.'),
(23, 'Caractéristiques', '14,07 kg sans pédale<br/>Fourche suspendue<br/>26 pouces<br/>Noir / Orange / Blanc<br/>M:165-175 , L:175-180'),
(23, 'Freins', 'Leviers de frein en aluminium : léger et précis.'),
(23, 'Pneus', 'Pneus Dry en 26 X 2.00, à profil polyvalent pour un excellent rendement et une très bonne accroche latérale.'),
(23, 'Poids', '14.07 kg sans pédale.'),
(23, 'Suspension', 'Pas de suspension arrière.'),
(23, 'Tailles', 'M|L|XL');

--
-- Dumping data for table `Product`
--

INSERT INTO `Product` (`id`, `description`, `disabled`, `endDate`, `name`, `startDate`) VALUES
(1, 'VTT energy X1', b'0', NULL, 'Energy X1', NULL),
(2, 'VTT Energy X2', b'0', NULL, 'Energy X2', NULL),
(3, 'VTC Lazer V2', b'1', NULL, 'Lazer V2', NULL),
(4, 'VTC Beam 3000', b'0', '2014-06-17 00:52:52', 'Beam 3000', '2014-06-18 00:52:52'),
(5, 'Michelin NX01 tire', b'0', NULL, 'Michelin NX01', NULL);

--
-- Dumping data for table `Product_Presentation`
--

INSERT INTO `Product_Presentation` (`catalogItemId`, `presentationId`) VALUES
(1, 22),
(1, 23),
(2, 24),
(2, 25);

--
-- Dumping data for table `Product_SKU`
--

INSERT INTO `Product_SKU` (`productId`, `skuId`, `orderIdx`) VALUES
(1, 1, 0),
(1, 2, 1),
(2, 3, 0),
(5, 4, 0),
(4, 6, 0);

--
-- Dumping data for table `SKU`
--

INSERT INTO `SKU` (`id`, `description`, `disabled`, `endDate`, `name`, `startDate`, `currency`, `price`, `quantity`, `reference`, `threshold`) VALUES
(1, 'Energy X1 - XL', b'0', NULL, 'Energy X1 - XL', NULL, 'EUR', 142, 100, 'X1213JJLB-1', 1),
(2, 'Energy X1 - L', b'0', NULL, 'Energy X1 - L', NULL, 'EUR', 132, 100, 'X1213JJLB-2', 1),
(3, 'Energy X2', b'0', NULL, 'Energy X2', NULL, NULL, 10, 100, 'X1213JJLB-3', 3),
(4, 'Michelin NX01', b'0', NULL, 'Michelin NX01', NULL, NULL, 10, 2, 'X1213JJLB-4', 3),
(5, 'Lazer V2', b'0', NULL, 'Lazer V2', NULL, NULL, 10, 100, 'X1213JJLB-5', 3),
(6, 'Beam 3000', b'0', NULL, 'Beam 3000', NULL, NULL, NULL, NULL, NULL, NULL);

--
-- Dumping data for table `SKU_Discount`
--

INSERT INTO `SKU_Discount` (`skuId`, `discountId`, `orderIdx`) VALUES
(5, 1, 0);

--
-- Dumping data for table `SKU_Presentation`
--

INSERT INTO `SKU_Presentation` (`catalogItemId`, `presentationId`) VALUES
(1, 26),
(2, 27);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;