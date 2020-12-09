--
-- Database: jeeshop
--

--
-- data for table Media
--

INSERT INTO media (id, uri) VALUES
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

SELECT setval('media_id_seq', (SELECT MAX(id) from media));

--
-- data for table Catalog
--

INSERT INTO catalog (id, description, disabled, endDate, name, startDate) VALUES
(1, 'Catalog of Hyperbike store', false, NULL, 'Hyperbike catalog', NULL);

SELECT setval('catalog_id_seq', (SELECT MAX(id) from catalog));

--
-- data for table Presentation
--

INSERT INTO presentation (id, displayName, promotion, features, locale, shortDescription, mediumDescription, longDescription, largeImage_id, smallImage_id, thumbnail_id, video_id) VALUES
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
(27, 'L', '', NULL, 'en', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(28, 'Frais de livraison gratuit pour votre première commande !', 'fa-truck', NULL, 'fr', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(29, 'Free delivery fee for first order!', 'fa-truck', NULL, 'en', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(30, '10 pourcent de remise dès 100€ d''achats !', 'fa-gift', NULL, 'fr', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(31, '10 percent off from 100€ of purchase!', 'fa-gift', NULL, 'en', NULL, NULL, NULL, NULL, NULL, NULL, NULL);

SELECT setval('presentation_id_seq', (SELECT MAX(id) from presentation));

--
-- data for table Product
--

INSERT INTO product (id, description, disabled, endDate, name, startDate) VALUES
(1, 'VTT energy X1', false, NULL, 'Energy X1', NULL),
(2, 'VTT Energy X2', false, NULL, 'Energy X2', NULL),
(3, 'VTC Lazer V2', true, NULL, 'Lazer V2', NULL),
(4, 'VTC Beam 3000', false, '2014-06-17 00:52:52', 'Beam 3000', '2014-06-18 00:52:52'),
(5, 'Michelin NX01 tire', false, NULL, 'Michelin NX01', NULL);

SELECT setval('product_id_seq', (SELECT MAX(id) from product));

--
-- data for table Category
--

INSERT INTO category (id, description, disabled, endDate, name, startDate) VALUES
(1, 'Bikes main category', false, NULL, 'Bikes', NULL),
(2, 'Accessories main category', false, NULL, 'Accessories', NULL),
(3, 'Tires rubric', false, NULL, 'Tires', NULL),
(4, 'Brackets rubric', false, NULL, 'Brackets', NULL),
(5, 'Handlebar rubric', false, NULL, 'Handlebar', NULL),
(6, 'Wheels rubric', false, '2014-06-19 00:52:52', 'Wheels', '2014-06-18 00:52:52'),
(7, 'Mountain Bikes category', false, NULL, 'Mountain Bikes', NULL),
(12, 'Racing Bikes category', false, NULL, 'Racing Bikes', NULL);

SELECT setval('category_id_seq', (SELECT MAX(id) from category));


--
-- data for table Category_Category
--

INSERT INTO category_category (parentCategoryId, childCategoryId, orderIdx) VALUES
(2, 3, 0),
(2, 4, 1),
(2, 5, 2),
(2, 6, 3),
(1, 7, 0),
(1, 12, 1);

--
-- data for table Catalog_Category
--

INSERT INTO catalog_category (catalogId, categoryId, orderIdx) VALUES
(1, 1, 0),
(1, 2, 1);

--
-- Contenu de la table Discount
--

INSERT INTO discount (id, description, disabled, endDate, name, startDate, discountValue, triggerValue, triggerRule, type, applicableTo, uniqueUse, usesPerCustomer, voucherCode) VALUES
  (2, 'Free delivery fee for first order', false, NULL, 'Order - Free delivery - first order', NULL, 12, 1, 'ORDER_NUMBER', 'SHIPPING_FEE_DISCOUNT_AMOUNT', 'ORDER', true, NULL, NULL),
  (5, '10 percent off for order amount of 100', false, NULL, '10 percent off', NULL, 10, 100, 'AMOUNT', 'DISCOUNT_RATE', 'ORDER', NULL, NULL, NULL);

SELECT setval('discount_id_seq', (SELECT MAX(id) from discount));

--
-- Contenu de la table Discount_Presentation
--

INSERT INTO discount_presentation (catalogItemId, presentationId) VALUES
  (2, 28),
  (2, 29),
  (5, 30),
  (5, 31);

--
-- data for table Product_Presentation
--

INSERT INTO product_presentation (catalogItemId, presentationId) VALUES
(1, 22),
(1, 23),
(2, 24),
(2, 25);

--
-- data for table Category_Presentation
--

INSERT INTO category_presentation (catalogItemId, presentationId) VALUES
(1, 13),
(1, 14),
(2, 15),
(2, 16),
(7, 17),
(12, 19),
(12, 20),
(7, 21);

--
-- data for table Category_Product
--

INSERT INTO category_product (categoryId, productId, orderIdx) VALUES
(7, 1, 0),
(7, 2, 1),
(12, 3, 0),
(12, 4, 1),
(3, 5, 0);

--
-- data for table Presentation_Feature
--

INSERT INTO presentation_feature (presentationId, name, value) VALUES
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
-- data for table SKU
--

INSERT INTO sku (id, description, disabled, endDate, name, startDate, currency, price, quantity, reference, threshold) VALUES
(1, 'Energy X1 - XL', false, NULL, 'Energy X1 - XL', NULL, 'EUR', 142, 100, 'X1213JJLB-1', 1),
(2, 'Energy X1 - L', false, NULL, 'Energy X1 - L', NULL, 'EUR', 132, 100, 'X1213JJLB-2', 1),
(3, 'Energy X2', false, NULL, 'Energy X2', NULL, NULL, 10, 100, 'X1213JJLB-3', 3),
(4, 'Michelin NX01', false, NULL, 'Michelin NX01', NULL, NULL, 10, 2, 'X1213JJLB-4', 3),
(5, 'Lazer V2', false, NULL, 'Lazer V2', NULL, NULL, 10, 100, 'X1213JJLB-5', 3),
(6, 'Beam 3000', false, NULL, 'Beam 3000', NULL, NULL, NULL, NULL, NULL, NULL);

SELECT setval('sku_id_seq', (SELECT MAX(id) from sku));


--
-- data for table Product_SKU
--

INSERT INTO product_sku (productId, skuId, orderIdx) VALUES
(1, 1, 0),
(1, 2, 1),
(2, 3, 0),
(5, 4, 0),
(4, 6, 0);

--
-- data for table SKU_Presentation
--

INSERT INTO sku_presentation (catalogItemId, presentationId) VALUES
(1, 26),
(2, 27);

--
-- Contenu de la table MailTemplate
--

INSERT INTO mailtemplate (id, name, locale, content, subject, creationDate, updateDate) VALUES
  (1, 'userRegistration', 'fr', '<div style="width:100%;text-align:center">\n    <a href="https://apps-jeeshop.rhcloud.com">\n        <img src="https://apps-jeeshop.rhcloud.com/jeeshop-store/images/logo.png" style=" width: 10em; height: auto; padding-bottom: 1em;" alt="Jeeshop store demo">\n    </a>\n</div>\n\n<h3>Bienvenue ${gender} ${firstname} ${lastname},</h3>\n\n<p>Vous venez de cr&eacute;er un compte <a href="https://apps-jeeshop.rhcloud.com"><em>Jeeshop store demo</em></a>.</p>\n\n<p>Nous vous invitons &agrave; copier le lien ci-dessous dans la barre d''adresse de votre navigateur internet afin d&#39;activer votre compte&nbsp;: <em>(1)</em></p>\n<pre>&nbsp;</pre>\n<pre>https://apps-jeeshop.rhcloud.com/jeeshop-store/#!/activation/${login}/${actionToken}</pre>\n<pre>&nbsp;</pre>\n<p>A tr&egrave;s bient&ocirc;t</p>', 'Activation de votre compte Jeeshop store demo', '2016-01-11 01:42:08', NULL),
  (2, 'userRegistration', 'en', '<div style="width:100%;text-align:center">\n    <a href="https://apps-jeeshop.rhcloud.com">\n        <img src="https://apps-jeeshop.rhcloud.com/jeeshop-store/images/logo.png" style=" width: 10em; height: auto; padding-bottom: 1em;" alt="Jeeshop store demo">\n    </a>\n</div>\n\n<h3>Welcome ${gender} ${firstname} ${lastname},</h3>\n\n<p>You have subscribed to <a href="https://apps-jeeshop.rhcloud.com"><em>Jeeshop store demo</em></a>.</p>\n\n<p>Please activate your account by clicking on link below : <em>(1)</em></p>\n<pre>&nbsp;</pre>\n<pre>https://apps-jeeshop.rhcloud.com/jeeshop-store/#!/activation/${login}/${actionToken}</pre>\n<pre>&nbsp;</pre>\n<p>Best regards</p>', 'Activation of your Jeeshop store demo account', '2016-01-11 01:43:11', NULL),
  (3, 'userResetPassword', 'fr', '<div style="width:100%;text-align:center">\n    <a href="https://apps-jeeshop.rhcloud.com">\n        <img src="https://apps-jeeshop.rhcloud.com/jeeshop-store/images/logo.png" style=" width: 10em; height: auto; padding-bottom: 1em;" alt="Jeeshop store demo">\n    </a>\n</div>\n\n<h3>Bonjour ${gender} ${firstname} ${lastname},</h3>\n\n<p>Vous venez d&#39;effectuer une demande de r&eacute;initialisation de votre mot de passe sur <a href="https://apps-jeeshop.rhcloud.com"><em>Jeeshop store demo</em></a>.</p>\n\n<p>Nous vous invitons &agrave; copier le lien ci-dessous dans la barre d''adresse de votre navigateur internet afin de r&eacute;initialiser votre mot de passe : <em>(1)</em></p>\n<pre>&nbsp;</pre>\n<pre>https://apps-jeeshop.rhcloud.com/jeeshop-store/#!/resetpassword/${login}/${actionToken}</pre>\n<pre>&nbsp;</pre>\n<p>A tr&egrave;s bient&ocirc;t,</p>', 'Réinitialisation de votre mot de passe Jeeshop store demo', '2016-01-11 01:44:19', NULL),
  (4, 'userResetPassword', 'en', '<div style="width:100%;text-align:center">\n    <a href="https://apps-jeeshop.rhcloud.com">\n        <img src="https://apps-jeeshop.rhcloud.com/jeeshop-store/images/logo.png" style=" width: 10em; height: auto; padding-bottom: 1em;" alt="Jeeshop store demo">\n    </a>\n</div>\n\n<h3>Hello ${gender} ${firstname} ${lastname},</h3>\n\n<p>You have have submitted the reset password from <a href="https://apps-jeeshop.rhcloud.com"><em>Jeeshop store demo</em></a>.</p>\n\n<p>Please click on link below to reset your password : <em>(1)</em></p>\n<pre>&nbsp;</pre>\n<pre>https://apps-jeeshop.rhcloud.com/jeeshop-store/#!/resetpassword/${login}/${actionToken}</pre>\n<pre>&nbsp;</pre>\n<p>Best regards</p>', 'Reset of your Jeeshop store demo password', '2016-01-11 01:45:12', NULL),
  (5, 'orderValidated', 'fr', '<div style="width:100%;text-align:center">\n    <a href="https://apps-jeeshop.rhcloud.com">\n        <img src="https://apps-jeeshop.rhcloud.com/jeeshop-store/images/logo.png" style=" width: 10em; height: auto; padding-bottom: 1em;" alt="Jeeshop store demo">\n    </a>\n</div>\n\n<h3>Bonjour ${user.gender} ${user.firstname} ${user.lastname},</h3>\n\n<p>Nous avons le plaisir de vous confirmer la validation de votre commande numéro <strong>${reference}</strong></p>\n\n<p>Vous recevrez prochainement un e-mail lors de son expédition par nos services.</p>\n\n<h3><em>Détail : </em></h3>\n\n<table style="width:100%;border-width:1px; border-style=solid; text-align:justify">\n    <thead>\n    <th></th>\n    <th>Article</th>\n    <th>Quantité</th>\n    <th>Prix</th>\n    </thead>\n    <tbody>\n    <#list items as item>\n    <tr>\n        <td style="text-align:center">\n            <img src="https://apps-jeeshop.rhcloud.com/jeeshop-media/${item.presentationImageURI}" style="width:4em;height:auto"></img>\n        </td>\n        <td>${item.displayName}</td>\n        <td>${item.quantity}</td>\n        <td>${item.price} € TTC</td>\n    </tr>\n    </#list>\n    <tr>\n        <td style="text-align:center">\n        </td>\n        <td>Livraison par transporteur</td>\n        <td></td>\n        <td>${deliveryFee} € TTC</td>\n    </tr>\n    <#list orderDiscounts as orderDiscount>\n    <tr>\n        <td style="text-align:center">\n        </td>\n        <td>${orderDiscount.displayName}</td>\n        <td></td>\n        <td>- ${orderDiscount.discountValue} <#if orderDiscount.rateType>%<#else>€ TTC</#if></td>\n    </tr>\n    </#list>\n    <tr>\n        <td></td>\n        <td></td>\n        <td><i>Total HT</i></td>\n        <td><i>#{price*100/(100+vat); m2M2} €</i></td>\n    </tr>\n    <tr>\n        <td></td>\n        <td></td>\n        <td><b><i>Total TTC</i><b></td>\n        <td><b><i>${price} €</i></b></td>\n    </tr>\n    </tbody>\n</table>\n<br/>\n\n<table style="width:100%">\n    <tr>\n        <td style="width:45%">\n            <h4>Adresse de livraison</h4>\n            <p>\n            ${deliveryAddress.gender}\n  ${deliveryAddress.firstname}\n  ${deliveryAddress.lastname}\n            </p>\n\n            <p>\n                <b><i>Société</i></b>\n                <br/>\n            ${deliveryAddress.company!''''}\n            </p>\n\n            <p>\n                <b><i>Adresse</i></b>\n                <br/>\n            ${deliveryAddress.street}\n                <br/>\n            ${deliveryAddress.city}\n                <br/>\n            ${deliveryAddress.zipCode}\n            </p>\n\n            <p>\n                <b><i>Pays</i></b>\n                <br/>\n            ${deliveryAddress.countryIso3Code}\n            </p>\n        </td>\n        <td style="width:10%"></td>\n        <td style="width:45%">\n            <h4>Adresse de facturation</h4>\n            <p>\n            ${billingAddress.gender}\n  ${billingAddress.firstname}\n  ${billingAddress.lastname}\n            </p>\n\n            <p>\n                <b><i>Société</i></b>\n                <br/>\n            ${billingAddress.company!''''}\n            </p>\n\n            <p>\n                <b><i>Adresse</i></b>\n                <br/>\n            ${billingAddress.street}\n                <br/>\n            ${billingAddress.city}\n                <br/>\n            ${billingAddress.zipCode}\n            </p>\n\n            <p>\n                <b><i>Pays</i></b>\n                <br/>\n            ${billingAddress.countryIso3Code}\n            </p>\n        </td>\n    </tr>\n</table>\n\n<p>Cordialement</p>', 'Confirmation de votre commande', '2016-01-11 01:46:33', NULL),
  (6, 'orderValidated', 'en', '<div style="width:100%;text-align:center">\n    <a href="https://apps-jeeshop.rhcloud.com">\n        <img src="https://apps-jeeshop.rhcloud.com/jeeshop-store/images/logo.png" style=" width: 10em; height: auto; padding-bottom: 1em;" alt="Jeeshop store demo">\n    </a>\n</div>\n\n<h3>Hello ${user.gender} ${user.firstname} ${user.lastname},</h3>\n\n<p>We are glad to confim the validation of your order <strong>${reference}</strong></p>\n\n<p>You will receive another e-mail when it will be shipped.</p>\n\n<h3><em>Details :</em></h3>\n\n<table style="width:100%;border-width:1px; border-style=solid; text-align:justify">\n    <thead>\n    <th></th>\n    <th>Item</th>\n    <th>Quantity</th>\n    <th>Price</th>\n    </thead>\n    <tbody>\n    <#list items as item>\n    <tr>\n        <td style="text-align:center">\n            <img src="https://apps-jeeshop.rhcloud.com/jeeshop-media/${item.presentationImageURI}" style="width:4em;height:auto"></img>\n        </td>\n        <td>${item.displayName}</td>\n        <td>${item.quantity}</td>\n        <td>${item.price} €</td>\n    </tr>\n    </#list>\n    <tr>\n        <td style="text-align:center">\n        </td>\n        <td>Package delivery company</td>\n        <td></td>\n        <td>${deliveryFee} €</td>\n    </tr>\n    <#list orderDiscounts as orderDiscount>\n    <tr>\n        <td style="text-align:center">\n        </td>\n        <td>${orderDiscount.displayName}</td>\n        <td></td>\n        <td>- ${orderDiscount.discountValue} <#if orderDiscount.rateType>%<#else>€</#if></td>\n    </tr>\n    </#list>\n    <tr>\n        <td></td>\n        <td></td>\n        <td><i>Total HT</i></td>\n        <td><i>#{price*100/(100+vat); m2M2} €</i></td>\n    </tr>\n    <tr>\n        <td></td>\n        <td></td>\n        <td><b><i>Total TTC</i><b></td>\n        <td><b><i>${price} €</i></b></td>\n    </tr>\n    </tbody>\n</table>\n<br/>\n\n<table style="width:100%">\n    <tr>\n        <td style="width:45%">\n            <h4>Delivery address</h4>\n            <p>\n            ${deliveryAddress.gender}\n  ${deliveryAddress.firstname}\n  ${deliveryAddress.lastname}\n            </p>\n\n            <p>\n                <b><i>Company</i></b>\n                <br/>\n            ${deliveryAddress.company!''''}\n            </p>\n\n            <p>\n                <b><i>Address</i></b>\n                <br/>\n            ${deliveryAddress.street}\n                <br/>\n            ${deliveryAddress.city}\n                <br/>\n            ${deliveryAddress.zipCode}\n            </p>\n\n            <p>\n                <b><i>Country</i></b>\n                <br/>\n            ${deliveryAddress.countryIso3Code}\n            </p>\n        </td>\n        <td style="width:10%"></td>\n        <td style="width:45%">\n            <h4>Billing address</h4>\n            <p>\n            ${billingAddress.gender}\n  ${billingAddress.firstname}\n  ${billingAddress.lastname}\n            </p>\n\n            <p>\n                <b><i>Company</i></b>\n                <br/>\n            ${billingAddress.company!''''}\n            </p>\n\n            <p>\n                <b><i>Address</i></b>\n                <br/>\n            ${billingAddress.street}\n                <br/>\n            ${billingAddress.city}\n                <br/>\n            ${billingAddress.zipCode}\n            </p>\n\n            <p>\n                <b><i>Country</i></b>\n                <br/>\n            ${billingAddress.countryIso3Code}\n            </p>\n        </td>\n    </tr>\n</table>\n\n<p>Best regards</p>', 'Confirmation of your order', '2016-01-11 01:47:09', NULL);

SELECT setval('mailtemplate_id_seq', (SELECT MAX(id) from mailtemplate));


