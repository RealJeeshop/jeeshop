
alter table if exists catalog add column owner varchar(100);
alter table if exists category add column owner varchar(100);
alter table if exists product add column owner varchar(100);
alter table if exists sku add column owner varchar(100);
alter table if exists discount add column owner varchar(100);

update catalog set owner = 'admin@jeeshop.org';
update category set owner = 'admin@jeeshop.org';
update product set owner = 'admin@jeeshop.org';
update sku set owner = 'admin@jeeshop.org';
update discount set owner = 'admin@jeeshop.org';

alter table if exists catalog alter column owner set not null;
alter table if exists category alter column owner set not null;
alter table if exists product alter column owner set not null;
alter table if exists sku alter column owner set not null;
alter table if exists discount alter column owner set not null;

create table if not exists store (
    id serial not null primary key,
    description varchar(255) null,
    disabled boolean null,
    enddate date null,
    name varchar(50) not null,
    startdate date null,
    owner varchar(100) not null);

create table if not exists store_catalog (
  store_id bigint not null,
  catalog_id bigint not null,
  orderidx int not null,
  primary key (store_id,orderidx));

create table if not exists premises_address (
  id serial not null primary key,
  city varchar(255) not null,
  street varchar(255)not null,
  zipcode varchar (10) not null,
  countryiso3code varchar (3) not null);

create table if not exists store_premises (
    id serial not null primary key,
    store_id bigint not null,
    address_id bigint not null
);

create table if not exists premises_schedules (
    id serial not null primary key,
    premises_id bigint not null,
    dayoftheweek integer not null,
    time_open time not null,
    time_close time not null);

create table if not exists store_presentation (
    catalogitemid bigint not null,
    presentationid bigint not null,
    primary key (catalogitemid,presentationid));

alter table store_presentation
  add constraint fk_store_presentation_catalog foreign key (catalogitemid) references store (id),
  add constraint fk_store_presentation_presentation foreign key (presentationid) references presentation (id);

alter table store_catalog
  add constraint fk_store_catalog_store foreign key (store_id) references store (id),
  add constraint fk_store_catalog_catalog foreign key (catalog_id) references catalog (id);

alter table store_premises
    add constraint fk_premises_address foreign key (address_id) references premises_address (id),
    add constraint fk_premises_store foreign key (store_id) references store (id);

alter table premises_schedules
     add constraint fk_schedules_premises foreign key (premises_id) references store_premises (id);

insert into "role" (id, name) values (4, 'store_admin');