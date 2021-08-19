# 202108-internship-loan-system


```SQL
CREATE TABLE credit_rating_search_historys(
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    NDI VARCHAR(30) NOT NULL,
    grade INT NOT NULL,
    created_date DATE NOT NULL default(NOW())
);

CREATE TABLE credit_rating_search_results(
    NDI VARCHAR(30) PRIMARY KEY,
    grade INT NOT NULL,
    history_id INT NOT NULL,
    FOREIGN KEY(history_id) references credit_rating_search_historys(history_id)
);

CREATE TABLE accounts(
    account_id INT PRIMARY KEY AUTO_INCREMENT,
    account_numbers VARCHAR(50) NOT NULL UNIQUE,
    NDI VARCHAR(30) NOT NULL,
    loan_limit INT NOT NULL,
    balance INT NOT NULL,
    grade INT NOT NULL,
    status VARCHAR(15) NOT NULL default("normal"),
    created_date DATE NOT NULL default(NOW()),
    loan_start_date DATE
);

ALTER TABLE accounts ADD INDEX (account_numbers);

CREATE TABLE account_transaction_historys(
    historys_id INT PRIMARY KEY AUTO_INCREMENT,
    account_id INT NOT NULL,
    account_numbers VARCHAR(30) NOT NULL,
    type VARCHAR(15) NOT NULL,
    created_date DATE NOT NULL default(NOW()),
    FOREIGN KEY(account_id) REFERENCES accounts(account_id),
    FOREIGN KEY(account_numbers) REFERENCES accounts(account_numbers)
);
```