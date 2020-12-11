
create table if not exists store (
    id serial not null primary key,
    description varchar(255) null,
    disabled boolean null,
    enddate date null,
    name varchar(50) not null,
    startdate date null,
    address_id bigint null,
    openingHours varchar(255) null);

create table if not exists schedules (
    id serial not null primary key,
    store_id bigint not null,
    dayoftheweek integer not null,
    time_open time not null,
    time_close time not null);

create table if not exists store_presentation (
    catalogitemid bigint not null,
    presentationid bigint not null,
    primary key (catalogitemid,presentationid));

alter table store
    add constraint fk_store_address foreign key (address_id) references address (id);

alter table schedules
    add constraint fk_schedules_store foreign key (store_id) references store (id);