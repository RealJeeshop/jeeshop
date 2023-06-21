alter table orders add column store_id bigint not null;
alter table orders add constraint fk_orders_store foreign key (store_id) references store (id);