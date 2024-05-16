-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;

-- Create the accounts table with the identity column
CREATE TABLE IF NOT EXISTS accounts (
                                        id BIGINT GENERATED ALWAYS AS IDENTITY,
                                        account_name VARCHAR(255),
    balance DOUBLE PRECISION NOT NULL CHECK (balance >= 0),
    is_active BOOLEAN NOT NULL,
    PRIMARY KEY (id)
    );

-- Create the transactions table with the identity column and foreign key
CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGINT GENERATED ALWAYS AS IDENTITY,
                                            account_id BIGINT NOT NULL REFERENCES accounts(id),
    date DATE NOT NULL,
    amount DOUBLE PRECISION NOT NULL CHECK (amount >= 0),
    PRIMARY KEY (id)
    );

-- Insert records into accounts table without OVERRIDING SYSTEM VALUE
INSERT INTO accounts (id, account_name, balance, is_active)
    OVERRIDING SYSTEM VALUE VALUES (1, 'Test Account 1', 1000.0, TRUE);

INSERT INTO accounts (id, account_name, balance, is_active)
    OVERRIDING SYSTEM VALUE VALUES (2, 'Test Account 2', 2000.0, TRUE);

INSERT INTO accounts (id, account_name, balance, is_active)
    OVERRIDING SYSTEM VALUE VALUES (3, 'Test Account 3', 3000.0, TRUE);
