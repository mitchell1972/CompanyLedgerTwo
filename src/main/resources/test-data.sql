
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


