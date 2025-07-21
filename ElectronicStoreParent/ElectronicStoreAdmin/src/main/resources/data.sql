-- Products
INSERT INTO product (id, name, description, price, category, available, stock, created_at, updated_at, version)
VALUES (1, 'iPhone 16', 'Latest Apple smartphone', 688.99, 'Smartphone', TRUE, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO product (id, name, description, price, category, available, stock, created_at, updated_at, version)
VALUES (2, 'Samsung Galaxy S25 Ultra', 'Latest Samsung smartphone', 969.99, 'Smartphone', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO product (id, name, description, price, category, available, stock, created_at, updated_at, version)
VALUES (3, 'MacBook Pro', 'Apple M2 Pro laptop', 1999.00, 'Laptop', TRUE, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

ALTER TABLE product ALTER COLUMN id RESTART WITH 4;

-- Discount Deals
INSERT INTO discount_deal (id, type, discount_percent, buy_quantity, get_quantity, expires_at, created_at, updated_at, version)
VALUES (1, 'PERCENTAGE', 10.00, 0, 0, '2025-12-31T23:59:59', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO discount_deal (id, type, buy_quantity, get_quantity, expires_at, created_at, updated_at, version)
VALUES (2, 'BUY_X_GET_Y', 1, 1, '2025-12-31T23:59:59', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

ALTER TABLE discount_deal ALTER COLUMN id RESTART WITH 3;

-- Join table: discount_deal_product (discount_deal_id, product_id)
INSERT INTO discount_deal_product (discount_deal_id, product_id) VALUES (1, 1);
INSERT INTO discount_deal_product (discount_deal_id, product_id) VALUES (1, 2);
INSERT INTO discount_deal_product (discount_deal_id, product_id) VALUES (2, 3);
