--
-- db: jeeshop - postgres
--

create table if not exists catalog (
  id serial not null primary key,
  description varchar(255) null,
  disabled boolean null,
  enddate date null,
  name varchar(50) not null,
  startdate date null);


create table if not exists catalog_category (
  catalogid bigint not null,
  categoryid bigint not null,
  orderidx int not null,
  primary key (catalogid,orderidx));


create table if not exists catalog_presentation (
  catalogitemid bigint not null,
  presentationid bigint not null,
  primary key (catalogitemid,presentationid));


create table if not exists "category" (
  id serial not null primary key,
  description varchar(255) null,
  disabled boolean null,
  enddate date null,
  name varchar(50) not null,
  startdate date null);


create table if not exists category_category (
  parentcategoryid bigint not null,
  childcategoryid bigint not null,
  orderidx int not null,
  primary key (parentcategoryid,orderidx)
);


create table if not exists category_presentation (
  catalogitemid bigint not null,
  presentationid bigint not null,
  primary key (catalogitemid,presentationid));


create table if not exists category_product (
  categoryid bigint not null,
  productid bigint not null,
  orderidx int not null,
  primary key (categoryid,orderidx));

create table if not exists product (
  id serial not null primary key,
  description varchar(255) null,
  disabled boolean null,
  enddate date null,
  name varchar(50) not null,
  startdate date null);


create table if not exists product_discount (
  productid bigint not null,
  discountid bigint not null,
  orderidx int not null,
  primary key (productid,orderidx));


create table if not exists product_presentation (
  catalogitemid bigint not null,
  presentationid bigint not null,
  primary key (catalogitemid,presentationid));


create table if not exists product_sku (
  productid bigint not null,
  skuid bigint not null,
  orderidx int not null,
  primary key (productid,orderidx));


create table if not exists sku (
  id serial not null primary key,
  description varchar(255) null,
  disabled boolean null,
  enddate date null,
  name varchar(50) not null,
  startdate date null,
  currency varchar(3) null,
  price double precision null,
  quantity int null,
  reference varchar(50) null,
  threshold int null);


create table if not exists sku_discount (
  skuid bigint not null,
  discountid bigint not null,
  orderidx int not null,
  primary key (skuid,orderidx));


create table if not exists sku_presentation (
  catalogitemid bigint not null,
  presentationid bigint not null,
  primary key (catalogitemid,presentationid));

create table if not exists discount (
  id serial not null primary key,
  description varchar(255) null,
  disabled boolean null,
  enddate date null,
  name varchar(50) not null,
  startdate date null,
  discountvalue double precision null,
  triggervalue double precision null,
  triggerrule varchar(50) null,
  type varchar(50) null,
  applicableto varchar(10) not null ,
  uniqueuse boolean null,
  usespercustomer int null,
  vouchercode varchar(100) null);


create table if not exists discount_presentation (
  catalogitemid bigint not null,
  presentationid bigint not null,
  primary key (catalogitemid,presentationid));


create table if not exists media (
  id serial not null primary key,
  uri varchar(255) not null);

create table if not exists presentation (
  id serial not null primary key,
  displayname varchar(255) null,
  promotion varchar(255) null,
  features bytea,
  locale varchar(25) not null,
  shortdescription varchar(1000) null,
  mediumdescription varchar(2000) null,
  longdescription varchar(5000) null,
  largeimage_id bigint null,
  smallimage_id bigint null,
  thumbnail_id bigint null,
  video_id bigint null);

create table if not exists presentation_feature (
  presentationid bigint not null,
  name varchar(255) not null,
  value varchar(255) null,
  primary key (presentationid,name));

create table if not exists presentation_media (
  presentationid bigint not null,
  mediaid bigint not null,
  orderidx int not null,
  primary key (presentationid,orderidx));


create table if not exists "user" (
  id serial not null primary key,
  birthdate date null,
  creationdate date not null,
  updatedate date null,
  disabled boolean null,
  activated boolean null,
  newsletterssubscribed boolean null,
  actiontoken bytea null,
  firstname varchar(50) not null,
  lastname varchar(50) not null,
  gender varchar(30) not null,
  login varchar(255) not null unique,
  password varchar(100) not null,
  phonenumber varchar(30) null,
  address_id bigint null,
  deliveryaddress_id bigint null,
  preferredlocale varchar(25) null);


create table if not exists orders (
  id serial not null primary key ,
  user_id bigint not null,
  transactionid varchar(255) null,
  deliveryaddress_id bigint null,
  billingaddress_id bigint null,
  status varchar(30) not null,
  creationdate date not null,
  updatedate date null,
  paymentdate date null,
  deliverydate date null,
  parceltrackingkey varchar(50) null,
  price double precision null);

create table if not exists orderitem (
  id serial not null primary key ,
  order_id bigint not null,
  sku_id bigint null,
  product_id bigint null,
  quantity int null,
  price double precision null);

create table if not exists orderdiscount(
  order_id bigint not null,
  discount_id bigint not null,
  discountvalue double precision null,
  unique (order_id,discount_id));

create table if not exists address (
  id serial not null primary key,
  city varchar(255) not null,
  street varchar(255)not null,
  zipcode varchar (10) not null,
  countryiso3code varchar (3) not null,
  gender varchar(30)  not null,
  firstname varchar(50)  not null,
  lastname varchar(50)  not null,
  company varchar(100) null);

create table if not exists "role" (
  id serial not null primary key,
  name varchar(255) not null unique);

create table if not exists user_role (
  userid bigint not null,
  roleid bigint not null,
  primary key (userid,roleid));

create table if not exists mailtemplate (
  id serial not null primary key,
  name varchar(100) not null,
  locale varchar(25) null,
  content text not null,
  subject varchar (255) not null,
  creationdate date not null,
  updatedate date null,
  unique (name,locale));

create table if not exists mailtemplate_media (
  mailtemplateid bigint not null,
  mediaid bigint not null,
  primary key (mailtemplateid,mediaid));

create table if not exists newsletter (
  id serial not null primary key,
  name varchar(100) not null,
  mailtemplatename varchar(100) not null,
  creationdate date not null,
  updatedate date null,
  duedate date null);

--
-- constraints
--

alter table catalog_category
  add constraint fk_catalog_category_catalog foreign key (catalogid) references catalog (id),
  add constraint fk_catalog_category_category foreign key (categoryid) references "category" (id);

alter table catalog_presentation
  add constraint fk_catalog_presentation_catalog foreign key (catalogitemid) references catalog (id),
  add constraint fk_catalog_presentation_presentation foreign key (presentationid) references presentation (id);

alter table category_category
  add constraint fk_category_category_category1 foreign key (parentcategoryid) references "category" (id),
  add constraint fk_category_category_category2 foreign key (childcategoryid) references "category" (id);

alter table category_presentation
  add constraint fk_category_presentation_category foreign key (catalogitemid) references "category" (id),
  add constraint fk_category_presentation_presentation foreign key (presentationid) references presentation (id);

alter table category_product
  add constraint fk_category_product_category foreign key (categoryid) references "category" (id),
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

alter table "user"
  add constraint fk_user_address1 foreign key (deliveryaddress_id) references address (id),
  add constraint fk_user_address2 foreign key (address_id) references address (id);

alter table orders
  add constraint fk_orders_address1 foreign key (deliveryaddress_id) references address (id),
  add constraint fk_orders_address2 foreign key (billingaddress_id) references address (id),
  add constraint fk_orders_user foreign key (user_id) references "user" (id);

alter table orderitem
  add constraint fk_orderitem_order foreign key (order_id) references orders (id);

alter table orderdiscount
  add constraint fk_orderdiscount_orders foreign key (order_id) references orders(id);


alter table mailtemplate_media
  add constraint fk_mailtemplate_media_mailtemplate foreign key (mailtemplateid) references mailtemplate (id),
  add constraint fk_mailtemplate_media_media foreign key (mediaid) references media (id);

alter table user_role
  add constraint fk_user_role_user foreign key (userid) references "user" (id),
  add constraint fk_user_role_role foreign key (roleid) references "role" (id);

--
-- default users / roles
--

insert into "user" (birthdate, creationdate, activated, gender, firstname, lastname, login, password, phonenumber, address_id, deliveryaddress_id)
values ('2014-06-18 00:00:00', '2014-07-20 00:00:00', true, 'm.' , 'gerald', 'min', 'admin@jeeshop.org', '$2a$10$2yACdyyMXZfEJtsfP8MLKu63a0kKZa8gUSrWJkYDNqMQyKax3geLC', '', null, null);

insert into "role" (id, name) values (1, 'user');
insert into "role" (id, name) values (2, 'admin');
insert into "role" (id, name) values (3, 'adminro');

insert into user_role (userid ,roleid) values ('1', '1');
insert into user_role (userid ,roleid) values ('1', '2');
