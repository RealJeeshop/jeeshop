--
-- db: jeeshop - mysql5
--

create table if not exists catalog (
  id bigint(20) not null auto_increment,
  description varchar(255) null,
  disabled bit(1) null,
  enddate datetime null,
  name varchar(50) not null,
  startdate datetime null,
  primary key (id)
);


create table if not exists catalog_category (
  catalogid bigint(20) not null,
  categoryid bigint(20) not null,
  orderidx int(11) not null,
  primary key (catalogid,orderidx)
);


create table if not exists catalog_presentation (
  catalogitemid bigint(20) not null,
  presentationid bigint(20) not null,
  primary key (catalogitemid,presentationid)
);


create table if not exists category (
  id bigint(20) not null auto_increment,
  description varchar(255) null,
  disabled bit(1) null,
  enddate datetime null,
  name varchar(50) not null,
  startdate datetime null,
  primary key (id)
);


create table if not exists category_category (
  parentcategoryid bigint(20) not null,
  childcategoryid bigint(20) not null,
  orderidx int(11) not null,
  primary key (parentcategoryid,orderidx)
);


create table if not exists category_presentation (
  catalogitemid bigint(20) not null,
  presentationid bigint(20) not null,
  primary key (catalogitemid,presentationid)
);


create table if not exists category_product (
  categoryid bigint(20) not null,
  productid bigint(20) not null,
  orderidx int(11) not null,
  primary key (categoryid,orderidx)
);

create table if not exists product (
  id bigint(20) not null auto_increment,
  description varchar(255) null,
  disabled bit(1) null,
  enddate datetime null,
  name varchar(50) not null,
  startdate datetime null,
  primary key (id)
);


create table if not exists product_discount (
  productid bigint(20) not null,
  discountid bigint(20) not null,
  orderidx int(11) not null,
  primary key (productid,orderidx)
);


create table if not exists product_presentation (
  catalogitemid bigint(20) not null,
  presentationid bigint(20) not null,
  primary key (catalogitemid,presentationid)
);


create table if not exists product_sku (
  productid bigint(20) not null,
  skuid bigint(20) not null,
  orderidx int(11) not null,
  primary key (productid,orderidx)
);


create table if not exists sku (
  id bigint(20) not null auto_increment,
  description varchar(255) null,
  disabled bit(1) null,
  enddate datetime null,
  name varchar(50) not null,
  startdate datetime null,
  currency varchar(3) null,
  price double null,
  quantity int(11) null,
  reference varchar(50) null,
  threshold int(11) null,
  primary key (id)
);


create table if not exists sku_discount (
  skuid bigint(20) not null,
  discountid bigint(20) not null,
  orderidx int(11) not null,
  primary key (skuid,orderidx)
);


create table if not exists sku_presentation (
  catalogitemid bigint(20) not null,
  presentationid bigint(20) not null,
  primary key (catalogitemid,presentationid)
);

create table if not exists discount (
  id bigint(20) not null auto_increment,
  description varchar(255) null,
  disabled bit(1) null,
  enddate datetime null,
  name varchar(50) not null,
  startdate datetime null,
  discountvalue double null,
  triggervalue double null,
  triggerrule varchar(50) null,
  type varchar(50) null,
  applicableto varchar(10) not null ,
  uniqueuse bit(1) null,
  usespercustomer int(11) null,
  vouchercode varchar(100) null,
  primary key (id)
);


create table if not exists discount_presentation (
  catalogitemid bigint(20) not null,
  presentationid bigint(20) not null,
  primary key (catalogitemid,presentationid)
);


create table if not exists media (
  id bigint(20) not null auto_increment,
  uri varchar(255) not null,
  primary key (id)
);

create table if not exists presentation (
  id bigint(20) not null auto_increment,
  displayname varchar(255) null,
  promotion varchar(255) null,
  features tinyblob,
  locale varchar(25) not null,
  shortdescription varchar(1000) null,
  mediumdescription varchar(2000) null,
  longdescription varchar(5000) null,
  largeimage_id bigint(20) null,
  smallimage_id bigint(20) null,
  thumbnail_id bigint(20) null,
  video_id bigint(20) null,
  primary key (id)
);

create table if not exists presentation_feature (
  presentationid bigint(20) not null,
  name varchar(255) not null,
  value varchar(255) null,
  primary key (presentationid,name)
);

create table if not exists presentation_media (
  presentationid bigint(20) not null,
  mediaid bigint(20) not null,
  orderidx int(11) not null,
  primary key (presentationid,orderidx)
);


create table if not exists user (
  id bigint(20) not null auto_increment,
  birthdate datetime null,
  creationdate datetime not null,
  updatedate datetime null,
  disabled bit(1) null,
  activated bit(1) null,
  newsletterssubscribed bit(1) null,
  actiontoken binary (16) null,
  firstname varchar(50) not null,
  lastname varchar(50) not null,
  gender varchar(30) not null,
  login varchar(255) not null,
  password varchar(100) not null,
  phonenumber varchar(30) null,
  address_id bigint(20) null,
  deliveryaddress_id bigint(20) null,
  preferredlocale varchar(25) null,
  primary key (id),
  unique key uk_login (login)
);

create table if not exists orders (
  id bigint(20) not null auto_increment,
  user_id bigint(20) not null,
  transactionid varchar(255) null,
  deliveryaddress_id bigint(20) null,
  billingaddress_id bigint(20) null,
  status varchar(30) not null,
  creationdate datetime not null,
  updatedate datetime null,
  paymentdate datetime null,
  deliverydate datetime null,
  parceltrackingkey varchar(50) null,
  price double null,
  primary key (id)
);

create table if not exists orderitem (
  id bigint(20) not null auto_increment,
  order_id bigint(20) not null,
  sku_id bigint (20) null,
  product_id bigint (20) null,
  quantity int (11) null,
  price double null,
  primary key (id)
);

create table if not exists orderdiscount(
  order_id bigint(20) not null,
  discount_id bigint(20) not null,
  discountvalue double null,
  unique key uk_order_discountids (order_id,discount_id)
);

create table if not exists address (
  id bigint(20) not null auto_increment,
  city varchar(255) not null,
  street varchar(255)not null,
  zipcode varchar (10) not null,
  countryiso3code varchar (3) not null,
  gender varchar(30)  not null,
  firstname varchar(50)  not null,
  lastname varchar(50)  not null,
  company varchar(100) null,
  primary key (id)
);

create table if not exists role (
  id bigint(20) not null auto_increment,
  name varchar(255) not null,
  primary key (id),
  unique key uk_role_name (name)
);

create table if not exists user_role (
  userid bigint(20) not null,
  roleid bigint(20) not null,
  primary key (userid,roleid)
);

create table if not exists mailtemplate (
  id bigint(20) not null auto_increment,
  name varchar(100) not null,
  locale varchar(25) null,
  content text not null,
  subject varchar (255) not null,
  creationdate datetime not null,
  updatedate datetime null,
  primary key (id),
  unique key uk_mailtemplate_name (name,locale)
);

create table if not exists mailtemplate_media (
  mailtemplateid bigint(20) not null,
  mediaid bigint(20) not null,
  primary key (mailtemplateid,mediaid)
);

create table if not exists newsletter (
  id bigint(20) not null auto_increment,
  name varchar(100) not null,
  mailtemplatename varchar(100) not null,
  creationdate datetime not null,
  updatedate datetime null,
  duedate datetime null,
  primary key (id)
);

--
-- constraints
--

alter table catalog_category
  add constraint fk_catalog_category_catalog foreign key (catalogid) references catalog (id),
  add constraint fk_catalog_category_category foreign key (categoryid) references category (id);

alter table catalog_presentation
  add constraint fk_catalog_presentation_catalog foreign key (catalogitemid) references catalog (id),
  add constraint fk_catalog_presentation_presentation foreign key (presentationid) references presentation (id);

alter table category_category
  add constraint fk_category_category_category1 foreign key (parentcategoryid) references category (id),
  add constraint fk_category_category_category2 foreign key (childcategoryid) references category (id);

alter table category_presentation
  add constraint fk_category_presentation_category foreign key (catalogitemid) references category (id),
  add constraint fk_category_presentation_presentation foreign key (presentationid) references presentation (id);

alter table category_product
  add constraint fk_category_product_category foreign key (categoryid) references category (id),
  add constraint fk_category_product_product foreign key (productid) references product (id);

alter table discount_presentation
  add constraint fk_discount_presentation_discount foreign key (catalogitemid) references discount (id),
  add constraint fk_discount_presentation_presentation foreign key (presentationid) references presentation (id);

alter table presentation
  add constraint fk_presentation_media1 foreign key (video_id) references media (id),
  add constraint fk_presentation_media2 foreign key (thumbnail_id) references media (id),
  add constraint fk_presentation_media3 foreign key (smallimage_id) references media (id),
  add constraint fk_presentation_media4 foreign key (largeimage_id) references media (id);

alter table presentation_media
  add constraint fk_presentation_media_presentation foreign key (presentationid) references presentation (id),
  add constraint fk_presentation_media_media foreign key (mediaid) references media (id);

alter table presentation_feature
add constraint fk_presentation_feature_presentation foreign key (presentationid) references presentation (id);


alter table product_discount
  add constraint fk_product_discount_product foreign key (productid) references product (id),
  add constraint fk_product_discount_discount foreign key (discountid) references discount (id);

alter table product_presentation
  add constraint fk_product_presentation_product foreign key (catalogitemid) references product (id),
  add constraint fk_product_presentation_presentation foreign key (presentationid) references presentation (id);

alter table product_sku
  add constraint fk_product_sku_product foreign key (productid) references product (id),
  add constraint fk_product_sku_sku foreign key (skuid) references sku (id);

alter table sku_discount
  add constraint fk_sku_discount_sku foreign key (skuid) references sku (id),
  add constraint fk_sku_discount_discount foreign key (discountid) references discount (id);

alter table sku_presentation
  add constraint fk_sku_presentation_sku foreign key (catalogitemid) references sku (id),
  add constraint fk_sku_presentation_presentation foreign key (presentationid) references presentation (id);

alter table user
  add constraint fk_user_address1 foreign key (deliveryaddress_id) references address (id),
  add constraint fk_user_address2 foreign key (address_id) references address (id);

alter table orders
  add constraint fk_orders_address1 foreign key (deliveryaddress_id) references address (id),
  add constraint fk_orders_address2 foreign key (billingaddress_id) references address (id),
  add constraint fk_orders_user foreign key (user_id) references user (id);

alter table orderitem
  add constraint fk_orderitem_order foreign key (order_id) references orders (id);

alter table orderdiscount
  add constraint fk_orderdiscount_orders foreign key (order_id) references orders(id);


alter table mailtemplate_media
  add constraint fk_mailtemplate_media_mailtemplate foreign key (mailtemplateid) references mailtemplate (id),
  add constraint fk_mailtemplate_media_media foreign key (mediaid) references media (id);

alter table user_role
  add constraint fk_user_role_user foreign key (userid) references user (id),
  add constraint fk_user_role_role foreign key (roleid) references role (id);

--
-- default users / roles
--

insert into user (birthdate, creationdate, activated, gender, firstname, lastname, login, password, phonenumber, address_id, deliveryaddress_id)
values ('2014-06-18 00:00:00', '2014-07-20 00:00:00', 1, 'm.' , 'gerald', 'min', 'admin@jeeshop.org', 'DjYu7nlNFk6BdxO+LwxZJ3mBAfxgwytTS2cVRbmnIO8=', '', null, null);

insert into role (id, name) values (1, 'user');
insert into role (id, name) values (2, 'admin');
insert into role (id, name) values (3, 'adminro');

insert into user_role (userid ,roleid) values ('1', '1');
insert into user_role (userid ,roleid) values ('1', '2');
