create schema if not exists visita collate utf8mb4_0900_ai_ci;

use visita;

create table if not exists bookings
(
	booking_id varchar(255) not null,
	booking_date datetime(6) null,
	num_adults int not null,
	num_children int null,
	special_request varchar(500) null,
	status enum('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') null,
	total_price decimal(15,3) not null,
	promotion_id varchar(255) null,
	staff_id varchar(255) null,
	tour_id varchar(255) null,
	user_id varchar(255) null
);

alter table bookings
	add primary key (booking_id);

create table if not exists chat_messages
(
	message_id varchar(255) not null,
	content text not null,
	created_at datetime(6) null,
	is_read bit null,
	is_staff bit null,
	session_id varchar(255) null
);

alter table chat_messages
	add primary key (message_id);

create table if not exists chat_sessions
(
	session_id varchar(255) not null,
	created_at datetime(6) null,
	status enum('OPEN', 'CLOSED') null,
	updated_at datetime(6) null,
	staff_id varchar(255) null,
	user_id varchar(255) null
);

alter table chat_sessions
	add primary key (session_id);

alter table chat_messages
	add constraint FK3cpkdtwdxndrjhrx3gt9q5ux9
		foreign key (session_id) references chat_sessions (session_id);

create table if not exists favorites
(
	favorite_id varchar(255) not null,
	created_at datetime(6) null,
	tour_id varchar(255) not null,
	user_id varchar(255) not null
);

alter table favorites
	add primary key (favorite_id);

alter table favorites
	add constraint UK1n7ljfket3c6e8gxgbh0l70k9
		unique (user_id, tour_id);

create table if not exists history
(
	history_id varchar(255) not null,
	action_type varchar(100) null,
	timestamp datetime(6) null,
	tour_id varchar(255) null,
	user_id varchar(255) null
);

create index idx_history_tour
	on history (tour_id);

create index idx_history_user
	on history (user_id);

alter table history
	add primary key (history_id);

create table if not exists invalidated_tokens
(
	id varchar(255) not null,
	expiry_time datetime(6) null
);

alter table invalidated_tokens
	add primary key (id);

create table if not exists invoices
(
	invoice_id varchar(255) not null,
	amount decimal(15,2) not null,
	details varchar(500) null,
	issued_date date null,
	booking_id varchar(255) null
);

alter table invoices
	add primary key (invoice_id);

alter table invoices
	add constraint FKb9bhb7xre5v64qvjeholh3qj0
		foreign key (booking_id) references bookings (booking_id);

create table if not exists payments
(
	payment_id varchar(255) not null,
	amount decimal(15,2) not null,
	payment_date datetime(6) null,
	payment_method varchar(50) null,
	status enum('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') null,
	transaction_id varchar(100) null,
	booking_id varchar(255) null
);

alter table payments
	add primary key (payment_id);

alter table payments
	add constraint FKc52o2b1jkxttngufqp3t7jr3h
		foreign key (booking_id) references bookings (booking_id);

create table if not exists promotions
(
	promotion_id varchar(255) not null,
	code varchar(50) null,
	description varchar(255) null,
	discount_amount decimal(15,2) null,
	discount_percent decimal(5,2) null,
	end_date date null,
	is_active bit not null,
	quantity int null,
	start_date date null,
	version bigint null
);

alter table promotions
	add primary key (promotion_id);

alter table bookings
	add constraint FKk4byobgkjv3y3952wwpxyep7o
		foreign key (promotion_id) references promotions (promotion_id);

alter table promotions
	add constraint UKjdho73ymbyu46p2hh562dk4kk
		unique (code);

create table if not exists refresh_tokens
(
	id varchar(255) not null,
	expiry_date datetime(6) null,
	token text null,
	user_id varchar(255) not null
);

alter table refresh_tokens
	add primary key (id);

create table if not exists reviews
(
	review_id varchar(255) not null,
	comment longtext null,
	created_at datetime(6) null,
	is_visible bit null,
	rating int null,
	tour_id varchar(255) null,
	user_id varchar(255) null,
	booking_id varchar(255) null
);

alter table reviews
	add primary key (review_id);

alter table reviews
	add constraint UK3p9j9vyr1qofbcxju65es206r
		unique (booking_id);

alter table reviews
	add constraint FK28an517hrxtt2bsg93uefugrm
		foreign key (booking_id) references bookings (booking_id);

create table if not exists roles
(
	role_name varchar(50) not null,
	description varchar(255) null
);

alter table roles
	add primary key (role_name);

create table if not exists tour_images
(
	image_id varchar(255) not null,
	description varchar(255) null,
	image_url varchar(500) not null,
	tour_id varchar(255) null
);

alter table tour_images
	add primary key (image_id);

create table if not exists tours
(
	tour_id varchar(255) not null,
	availability int null,
	capacity int not null,
	category enum('ADVENTURE', 'BEACH', 'CITY', 'CULTURE', 'EXPLORATION', 'FOOD', 'NATURE') null,
	description longtext null,
	destination varchar(255) null,
	duration varchar(50) null,
	end_date date null,
	is_active bit not null,
	itinerary longtext null,
	price_adult decimal(15,2) not null,
	price_child decimal(15,2) not null,
	region enum('CENTRAL', 'NORTH', 'SOUTH') null,
	start_date date null,
	title varchar(255) not null,
	staff_id varchar(255) null,
	version bigint null
);

alter table tours
	add primary key (tour_id);

alter table bookings
	add constraint FKi21lisuytk5t7tlp7lv51ny2l
		foreign key (tour_id) references tours (tour_id);

alter table favorites
	add constraint FKmmamfjvnw2qm9kb1ke6tq25lc
		foreign key (tour_id) references tours (tour_id);

alter table history
	add constraint FKab69vehqn2edm5x16rdkojkq5
		foreign key (tour_id) references tours (tour_id);

alter table reviews
	add constraint FKg95fdc12cdl5o06q6la9jh0dm
		foreign key (tour_id) references tours (tour_id);

alter table tour_images
	add constraint FKth1m2rd6q6ltp8kii2msvfi5d
		foreign key (tour_id) references tours (tour_id);

create table if not exists users
(
	user_id varchar(255) not null,
	address varchar(255) null,
	created_at datetime(6) null,
	dob date null,
	email varchar(100) not null,
	full_name varchar(255) not null,
	gender enum('MALE', 'FEMALE', 'OTHER') null,
	is_active bit null,
	password varchar(255) not null,
	phone varchar(15) null,
	updated_at datetime(6) null,
	username varchar(50) null
);

alter table users
	add primary key (user_id);

alter table bookings
	add constraint FKeyog2oic85xg7hsu2je2lx3s6
		foreign key (user_id) references users (user_id);

alter table bookings
	add constraint FKq77lf1nt94ny672p97mnhhsnv
		foreign key (staff_id) references users (user_id);

alter table chat_sessions
	add constraint FK82ky97glaomlmhjqae1d0esmy
		foreign key (user_id) references users (user_id);

alter table chat_sessions
	add constraint FKiet8yvc26y0rlk9f2bt3xc1jr
		foreign key (staff_id) references users (user_id);

alter table favorites
	add constraint FKk7du8b8ewipawnnpg76d55fus
		foreign key (user_id) references users (user_id);

alter table history
	add constraint FKq4kh99ws9lhtls5i3o73gw30t
		foreign key (user_id) references users (user_id);

alter table refresh_tokens
	add constraint FK1lih5y2npsf8u5o3vhdb9y0os
		foreign key (user_id) references users (user_id);

alter table reviews
	add constraint FKcgy7qjc1r99dp117y9en6lxye
		foreign key (user_id) references users (user_id);

alter table tours
	add constraint FK95vlfb6kslxblh6n0j3nvcnps
		foreign key (staff_id) references users (user_id);

alter table users
	add constraint UK6dotkott2kjsp8vw4d0m25fb7
		unique (email);

alter table users
	add constraint UKr43af9ap4edm43mmtq01oddj6
		unique (username);

create table if not exists users_roles
(
	user_id varchar(255) not null,
	role_name varchar(50) not null
);

alter table users_roles
	add primary key (user_id, role_name);

alter table users_roles
	add constraint FK2o0jvgh89lemvvo17cbqvdxaa
		foreign key (user_id) references users (user_id);

alter table users_roles
	add constraint FKfddtbwrqg5sal9y57yyol7579
		foreign key (role_name) references roles (role_name);

