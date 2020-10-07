--
-- DB: jeeshop - DROP - MySql5
--

--
-- Tables
--
SET foreign_key_checks = 0;
drop table if exists address;
drop table if exists catalog;
drop table if exists catalog_category;
drop table if exists catalog_presentation;
drop table if exists category;
drop table if exists category_category;
drop table if exists category_presentation;
drop table if exists category_product;
drop table if exists country;
drop table if exists discount;
drop table if exists discount_presentation;
drop table if exists mailtemplate_media;
drop table if exists mailtemplate;
drop table if exists media;
drop table if exists presentation;
drop table if exists presentation_media;
drop table if exists presentation_feature;
drop table if exists product;
drop table if exists product_discount;
drop table if exists product_presentation;
drop table if exists product_sku;
drop table if exists role;
drop table if exists sku;
drop table if exists sku_discount;
drop table if exists sku_presentation;
drop table if exists orderitem;
drop table if exists orderdiscount;
drop table if exists orders;
drop table if exists user;
drop table if exists user_role;
drop table if exists newsletter;
SET foreign_key_checks = 1;
