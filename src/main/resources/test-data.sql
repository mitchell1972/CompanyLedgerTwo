DELETE FROM transactions;
DELETE FROM accounts;

CREATE TABLE IF NOT EXISTS accounts (
                                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                        account_name VARCHAR(255),
                                        balance DOUBLE NOT NULL CHECK (balance >= 0),
                                        is_active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                            account_id BIGINT NOT NULL,
                                            date DATE NOT NULL,
                                            amount DOUBLE NOT NULL CHECK (amount >= 0)
);

INSERT INTO accounts (id, account_name, balance, is_active) VALUES (1, 'Test Account 1', 1000.0, TRUE);
INSERT INTO accounts (id, account_name, balance, is_active) VALUES (2, 'Test Account 2', 2000.0, TRUE);
INSERT INTO accounts (id, account_name, balance, is_active) VALUES (3, 'Test Account 3', 3000.0, TRUE);

INSERT INTO transactions (account_id, date, amount) VALUES (1, '2024-05-01', 100.0);
INSERT INTO transactions (account_id, date, amount) VALUES (1, '2024-05-02', 200.0);
INSERT INTO transactions (account_id, date, amount) VALUES (2, '2024-05-03', 300.0);
INSERT INTO transactions (account_id, date, amount) VALUES (3, '2024-05-04', 400.0);
