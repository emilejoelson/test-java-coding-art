-- --------------------------------------------------------------------------------------------------------------

-- 1 -  SQL script for creating the product and category tables.
CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          uuid BINARY(16) UNIQUE NOT NULL,
                          title VARCHAR(255) UNIQUE NOT NULL,
                          description TEXT,
                          category_id BIGINT NOT NULL,
                          price DOUBLE,
                          isEnabled BOOLEAN DEFAULT TRUE,
                          createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updatedAt TIMESTAMP,
                          FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            uuid BINARY(16) UNIQUE NOT NULL,
                            name VARCHAR(255) UNIQUE NOT NULL,
                            description TEXT,
                            createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updatedAt TIMESTAMP
);

-- 2 -  SQL script for inserting 5 categories
INSERT INTO categories (uuid, name, description, createdAt, updatedAt)
VALUES
    (UNHEX(REPLACE(UUID(), '-', '')), 'Informatique', 'This category informatique is really optional for someone', '2024-07-04T00:55:47.736816', NULL),
    (UNHEX(REPLACE(UUID(), '-', '')), 'Books', 'Category for books', '2024-07-07T00:13:43.523135', NULL),
    (UNHEX(REPLACE(UUID(), '-', '')), 'Electronics', 'Category for electronic products', '2024-07-04T01:34:01.266864', NULL),
    (UNHEX(REPLACE(UUID(), '-', '')), 'Clothing', 'Category for clothing items', '2024-07-04T20:02:56.337673', NULL),
    (UNHEX(REPLACE(UUID(), '-', '')), 'Home Appliances', 'Category for home appliances', '2024-07-07T00:16:53.597229', NULL);


-----  - -------------------------------------------------------------------------------------------------------------------------------------------------------------------