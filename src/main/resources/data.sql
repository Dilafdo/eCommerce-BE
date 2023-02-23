CREATE TABLE payment_details (
    id int NOT NULL,
    price real,
    price_modifier real,
    final_price real,
    payment_method varchar(255),
    points real,
    date_time timestamp,
    PRIMARY KEY (id)
)