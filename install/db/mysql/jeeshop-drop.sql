--
-- DB: jeeshop - DROP - MySql5
--

--
-- Tables
--
SET foreign_key_checks = 0;
DROP TABLE IF EXISTS Address;
DROP TABLE IF EXISTS Catalog;
DROP TABLE IF EXISTS Catalog_Category;
DROP TABLE IF EXISTS Catalog_Presentation;
DROP TABLE IF EXISTS Category;
DROP TABLE IF EXISTS Category_Category;
DROP TABLE IF EXISTS Category_Presentation;
DROP TABLE IF EXISTS Category_Product;
DROP TABLE IF EXISTS Country;
DROP TABLE IF EXISTS Discount;
DROP TABLE IF EXISTS Discount_Presentation;
DROP TABLE IF EXISTS MailTemplate_Media;
DROP TABLE IF EXISTS MailTemplate;
DROP TABLE IF EXISTS Media;
DROP TABLE IF EXISTS Presentation;
DROP TABLE IF EXISTS Presentation_Media;
DROP TABLE IF EXISTS Presentation_Feature;
DROP TABLE IF EXISTS Product;
DROP TABLE IF EXISTS Product_Discount;
DROP TABLE IF EXISTS Product_Presentation;
DROP TABLE IF EXISTS Product_SKU;
DROP TABLE IF EXISTS Role;
DROP TABLE IF EXISTS SKU;
DROP TABLE IF EXISTS SKU_Discount;
DROP TABLE IF EXISTS SKU_Presentation;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS User_Role;
SET foreign_key_checks = 1;