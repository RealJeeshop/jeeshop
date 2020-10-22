--
-- DB: jeeshop - MySql5
--

CREATE TABLE IF NOT EXISTS Catalog (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  description varchar(255) NULL,
  disabled bit(1) NULL,
  endDate datetime NULL,
  name varchar(50) NOT NULL,
  startDate datetime NULL,
  PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS Catalog_Category (
  catalogId bigint(20) NOT NULL,
  categoryId bigint(20) NOT NULL,
  orderIdx int(11) NOT NULL,
  PRIMARY KEY (catalogId,orderIdx)
);


CREATE TABLE IF NOT EXISTS Catalog_Presentation (
  catalogItemId bigint(20) NOT NULL,
  presentationId bigint(20) NOT NULL,
  PRIMARY KEY (catalogItemId,presentationId)
);


CREATE TABLE IF NOT EXISTS Category (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  description varchar(255) NULL,
  disabled bit(1) NULL,
  endDate datetime NULL,
  name varchar(50) NOT NULL,
  startDate datetime NULL,
  PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS Category_Category (
  parentCategoryId bigint(20) NOT NULL,
  childCategoryId bigint(20) NOT NULL,
  orderIdx int(11) NOT NULL,
  PRIMARY KEY (parentCategoryId,orderIdx)
);


CREATE TABLE IF NOT EXISTS Category_Presentation (
  catalogItemId bigint(20) NOT NULL,
  presentationId bigint(20) NOT NULL,
  PRIMARY KEY (catalogItemId,presentationId)
);


CREATE TABLE IF NOT EXISTS Category_Product (
  categoryId bigint(20) NOT NULL,
  productId bigint(20) NOT NULL,
  orderIdx int(11) NOT NULL,
  PRIMARY KEY (categoryId,orderIdx)
);

CREATE TABLE IF NOT EXISTS Product (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  description varchar(255) NULL,
  disabled bit(1) NULL,
  endDate datetime NULL,
  name varchar(50) NOT NULL,
  startDate datetime NULL,
  PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS Product_Discount (
  productId bigint(20) NOT NULL,
  discountId bigint(20) NOT NULL,
  orderIdx int(11) NOT NULL,
  PRIMARY KEY (productId,orderIdx)
);


CREATE TABLE IF NOT EXISTS Product_Presentation (
  catalogItemId bigint(20) NOT NULL,
  presentationId bigint(20) NOT NULL,
  PRIMARY KEY (catalogItemId,presentationId)
);


CREATE TABLE IF NOT EXISTS Product_SKU (
  productId bigint(20) NOT NULL,
  skuId bigint(20) NOT NULL,
  orderIdx int(11) NOT NULL,
  PRIMARY KEY (productId,orderIdx)
);


CREATE TABLE IF NOT EXISTS SKU (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  description varchar(255) NULL,
  disabled bit(1) NULL,
  endDate datetime NULL,
  name varchar(50) NOT NULL,
  startDate datetime NULL,
  currency varchar(3) NULL,
  price double NULL,
  quantity int(11) NULL,
  reference varchar(50) NULL,
  threshold int(11) NULL,
  PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS SKU_Discount (
  skuId bigint(20) NOT NULL,
  discountId bigint(20) NOT NULL,
  orderIdx int(11) NOT NULL,
  PRIMARY KEY (skuId,orderIdx)
);


CREATE TABLE IF NOT EXISTS SKU_Presentation (
  catalogItemId bigint(20) NOT NULL,
  presentationId bigint(20) NOT NULL,
  PRIMARY KEY (catalogItemId,presentationId)
);

CREATE TABLE IF NOT EXISTS Discount (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  description varchar(255) NULL,
  disabled bit(1) NULL,
  endDate datetime NULL,
  name varchar(50) NOT NULL,
  startDate datetime NULL,
  discountValue double NULL,
  triggerValue double NULL,
  triggerRule varchar(50) NULL,
  type varchar(50) NULL,
  applicableTo varchar(10) NOT NULL ,
  uniqueUse bit(1) NULL,
  usesPerCustomer int(11) NULL,
  voucherCode varchar(100) NULL,
  PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS Discount_Presentation (
  catalogItemId bigint(20) NOT NULL,
  presentationId bigint(20) NOT NULL,
  PRIMARY KEY (catalogItemId,presentationId)
);


CREATE TABLE IF NOT EXISTS Media (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  uri varchar(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Presentation (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  displayName varchar(255) NULL,
  promotion varchar(255) NULL,
  features tinyblob,
  locale varchar(25) NOT NULL,
  shortDescription varchar(1000) NULL,
  mediumDescription varchar(2000) NULL,
  longDescription varchar(5000) NULL,
  largeImage_id bigint(20) NULL,
  smallImage_id bigint(20) NULL,
  thumbnail_id bigint(20) NULL,
  video_id bigint(20) NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Presentation_Feature (
  presentationId bigint(20) NOT NULL,
  name varchar(255) NOT NULL,
  value varchar(255) NULL,
  PRIMARY KEY (presentationId,name)
);

CREATE TABLE IF NOT EXISTS Presentation_Media (
  presentationId bigint(20) NOT NULL,
  mediaId bigint(20) NOT NULL,
  orderIdx int(11) NOT NULL,
  PRIMARY KEY (presentationId,orderIdx)
);


CREATE TABLE IF NOT EXISTS User (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  birthDate datetime NULL,
  creationDate datetime NOT NULL,
  updateDate datetime NULL,
  disabled bit(1) NULL,
  activated bit(1) NULL,
  newslettersSubscribed bit(1) NULL,
  actionToken BINARY (16) NULL,
  firstname varchar(50) NOT NULL,
  lastname varchar(50) NOT NULL,
  gender varchar(30) NOT NULL,
  login varchar(255) NOT NULL,
  password varchar(100) NOT NULL,
  phoneNumber varchar(30) NULL,
  address_id bigint(20) NULL,
  deliveryAddress_id bigint(20) NULL,
  preferredLocale varchar(25) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UK_Login (login)
);

CREATE TABLE IF NOT EXISTS Orders (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  user_id bigint(20) NOT NULL,
  transactionId VARCHAR(255) NULL,
  deliveryAddress_id bigint(20) NULL,
  billingAddress_id bigint(20) NULL,
  status varchar(30) NOT NULL,
  creationDate datetime NOT NULL,
  updateDate datetime NULL,
  paymentDate datetime NULL,
  deliveryDate datetime NULL,
  parcelTrackingKey VARCHAR(50) NULL,
  price double NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS OrderItem (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  order_id bigint(20) NOT NULL,
  sku_id bigint (20) NULL,
  product_id bigint (20) NULL,
  quantity int (11) NULL,
  price double NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS OrderDiscount(
  order_id bigint(20) NOT NULL,
  discount_id bigint(20) NOT NULL,
  discountValue double NULL,
  UNIQUE KEY UK_Order_discountIds (order_id,discount_id)
);

CREATE TABLE IF NOT EXISTS Address (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  city varchar(255) NOT NULL,
  street varchar(255)NOT NULL,
  zipCode varchar (10) NOT NULL,
  countryIso3Code varchar (3) NOT NULL,
  gender varchar(30)  NOT NULL,
  firstname varchar(50)  NOT NULL,
  lastname varchar(50)  NOT NULL,
  company varchar(100) NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Role (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UK_Role_name (name)
);

CREATE TABLE IF NOT EXISTS User_Role (
  userId bigint(20) NOT NULL,
  roleId bigint(20) NOT NULL,
  PRIMARY KEY (userId,roleId)
);

CREATE TABLE IF NOT EXISTS MailTemplate (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  locale varchar(25) NULL,
  content TEXT NOT NULL,
  subject varchar (255) NOT NULL,
  creationDate datetime NOT NULL,
  updateDate datetime NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UK_MailTemplate_name (name,locale)
);

CREATE TABLE IF NOT EXISTS MailTemplate_Media (
  mailTemplateId bigint(20) NOT NULL,
  mediaId bigint(20) NOT NULL,
  PRIMARY KEY (mailTemplateId,mediaId)
);

CREATE TABLE IF NOT EXISTS Newsletter (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  mailTemplateName varchar(100) NOT NULL,
  creationDate datetime NOT NULL,
  updateDate datetime NULL,
  dueDate datetime NULL,
  PRIMARY KEY (id)
);

--
-- Constraints
--

ALTER TABLE Catalog_Category
  ADD CONSTRAINT FK_Catalog_Category_Catalog FOREIGN KEY (catalogId) REFERENCES Catalog (id),
  ADD CONSTRAINT FK_Catalog_Category_Category FOREIGN KEY (categoryId) REFERENCES Category (id);

ALTER TABLE Catalog_Presentation
  ADD CONSTRAINT FK_Catalog_Presentation_Catalog FOREIGN KEY (catalogItemId) REFERENCES Catalog (id),
  ADD CONSTRAINT FK_Catalog_Presentation_Presentation FOREIGN KEY (presentationId) REFERENCES Presentation (id);

ALTER TABLE Category_Category
  ADD CONSTRAINT FK_Category_Category_Category1 FOREIGN KEY (parentCategoryId) REFERENCES Category (id),
  ADD CONSTRAINT FK_Category_Category_Category2 FOREIGN KEY (childCategoryId) REFERENCES Category (id);

ALTER TABLE Category_Presentation
  ADD CONSTRAINT FK_Category_Presentation_Category FOREIGN KEY (catalogItemId) REFERENCES Category (id),
  ADD CONSTRAINT FK_Category_Presentation_Presentation FOREIGN KEY (presentationId) REFERENCES Presentation (id);

ALTER TABLE Category_Product
  ADD CONSTRAINT FK_Category_Product_Category FOREIGN KEY (categoryId) REFERENCES Category (id),
  ADD CONSTRAINT FK_Category_Product_Product FOREIGN KEY (productId) REFERENCES Product (id);

ALTER TABLE Discount_Presentation
  ADD CONSTRAINT FK_Discount_Presentation_Discount FOREIGN KEY (catalogItemId) REFERENCES Discount (id),
  ADD CONSTRAINT FK_Discount_Presentation_Presentation FOREIGN KEY (presentationId) REFERENCES Presentation (id);

ALTER TABLE Presentation
  ADD CONSTRAINT FK_Presentation_Media1 FOREIGN KEY (video_id) REFERENCES Media (id),
  ADD CONSTRAINT FK_Presentation_Media2 FOREIGN KEY (thumbnail_id) REFERENCES Media (id),
  ADD CONSTRAINT FK_Presentation_Media3 FOREIGN KEY (smallImage_id) REFERENCES Media (id),
  ADD CONSTRAINT FK_Presentation_Media4 FOREIGN KEY (largeImage_id) REFERENCES Media (id);

ALTER TABLE Presentation_Media
  ADD CONSTRAINT FK_Presentation_Media_Presentation FOREIGN KEY (presentationId) REFERENCES Presentation (id),
  ADD CONSTRAINT FK_Presentation_Media_Media FOREIGN KEY (mediaId) REFERENCES Media (id);

ALTER TABLE Presentation_Feature
ADD CONSTRAINT FK_Presentation_Feature_Presentation FOREIGN KEY (presentationId) REFERENCES Presentation (id);


ALTER TABLE Product_Discount
  ADD CONSTRAINT FK_Product_Discount_Product FOREIGN KEY (productId) REFERENCES Product (id),
  ADD CONSTRAINT FK_Product_Discount_Discount FOREIGN KEY (discountId) REFERENCES Discount (id);

ALTER TABLE Product_Presentation
  ADD CONSTRAINT FK_Product_Presentation_Product FOREIGN KEY (catalogItemId) REFERENCES Product (id),
  ADD CONSTRAINT FK_Product_Presentation_Presentation FOREIGN KEY (presentationId) REFERENCES Presentation (id);

ALTER TABLE Product_SKU
  ADD CONSTRAINT FK_Product_SKU_Product FOREIGN KEY (productId) REFERENCES Product (id),
  ADD CONSTRAINT FK_Product_SKU_SKU FOREIGN KEY (skuId) REFERENCES SKU (id);

ALTER TABLE SKU_Discount
  ADD CONSTRAINT FK_SKU_Discount_SKU FOREIGN KEY (skuId) REFERENCES SKU (id),
  ADD CONSTRAINT FK_SKU_Discount_Discount FOREIGN KEY (discountId) REFERENCES Discount (id);

ALTER TABLE SKU_Presentation
  ADD CONSTRAINT FK_SKU_Presentation_SKU FOREIGN KEY (catalogItemId) REFERENCES SKU (id),
  ADD CONSTRAINT FK_SKU_Presentation_Presentation FOREIGN KEY (presentationId) REFERENCES Presentation (id);

ALTER TABLE User
  ADD CONSTRAINT FK_User_Address1 FOREIGN KEY (deliveryAddress_id) REFERENCES Address (id),
  ADD CONSTRAINT FK_User_Address2 FOREIGN KEY (address_id) REFERENCES Address (id);

ALTER TABLE Orders
  ADD CONSTRAINT FK_Orders_Address1 FOREIGN KEY (deliveryAddress_id) REFERENCES Address (id),
  ADD CONSTRAINT FK_Orders_Address2 FOREIGN KEY (billingAddress_id) REFERENCES Address (id),
  ADD CONSTRAINT FK_Orders_User FOREIGN KEY (user_id) REFERENCES User (id);

ALTER TABLE OrderItem
  ADD CONSTRAINT FK_OrderItem_Order FOREIGN KEY (order_id) REFERENCES Orders (id);

ALTER TABLE OrderDiscount
  ADD CONSTRAINT FK_OrderDiscount_Orders FOREIGN KEY (order_id) REFERENCES Orders(id);


ALTER TABLE MailTemplate_Media
  ADD CONSTRAINT FK_MailTemplate_Media_MailTemplate FOREIGN KEY (mailTemplateId) REFERENCES MailTemplate (id),
  ADD CONSTRAINT FK_MailTemplate_Media_Media FOREIGN KEY (mediaId) REFERENCES Media (id);

ALTER TABLE User_Role
  ADD CONSTRAINT FK_User_Role_User FOREIGN KEY (userId) REFERENCES User (id),
  ADD CONSTRAINT FK_User_Role_Role FOREIGN KEY (roleId) REFERENCES Role (id);

--
-- Default Users / Roles
--

INSERT INTO User (birthDate, creationDate, activated, gender, firstname, lastname, login, password, phoneNumber, address_id, deliveryAddress_id)
VALUES ('2014-06-18 00:00:00', '2014-07-20 00:00:00', 1, 'M.' , 'Gerald', 'Min', 'admin@jeeshop.org', 'DjYu7nlNFk6BdxO+LwxZJ3mBAfxgwytTS2cVRbmnIO8=', '', NULL, NULL);

INSERT INTO Role (id, name) VALUES (1, 'user');
INSERT INTO Role (id, name) VALUES (2, 'admin');
INSERT INTO Role (id, name) VALUES (3, 'adminRO');

INSERT INTO User_Role (userId ,roleId) VALUES ('1', '1');
INSERT INTO User_Role (userId ,roleId) VALUES ('1', '2');