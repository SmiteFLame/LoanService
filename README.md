# 202108-internship-loan-system

## REST 명세서
![REST명세서](./image/REST명세서.png)

## 데이터베이스

### E-R 다이어그램
![REST명세서](./image/ERD.png)


### 데이터베이스 Create 생성문

```SQL

-- UserDB
CREATE TABLE users(
    NDI VARCHAR(36) PRIMARY KEY,
    email VARCHAR(50) not null unique,
    user_name VARCHAR(10) not null,
    age INT NOT NULL,
    salary INT NOT NULL
);

-- CreditDB
CREATE TABLE credit_rating_search_historys(
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    NDI VARCHAR(36) NOT NULL,
    grade INT NOT NULL,
    created_date DATETIME NOT NULL default(NOW())
);

CREATE TABLE credit_rating_search_results(
    NDI VARCHAR(36) PRIMARY KEY,
    grade INT NOT NULL,
    history_id INT NOT NULL,
    FOREIGN KEY(history_id) references credit_rating_search_historys(history_id)
);

-- AccountDB
CREATE TABLE accounts(
    account_id INT PRIMARY KEY AUTO_INCREMENT,
    account_numbers VARCHAR(50) NOT NULL UNIQUE,
    NDI VARCHAR(36) NOT NULL,
    loan_limit INT NOT NULL,
    balance INT NOT NULL,
    grade INT NOT NULL,
    status VARCHAR(15) NOT NULL default("normal"),
    created_date DATETIME NOT NULL default(NOW()),
    loan_start_date DATETIME
);

ALTER TABLE accounts ADD INDEX (account_numbers);

CREATE TABLE account_transaction_historys(
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    account_id INT NOT NULL,
    amount INT NOT NULL,
    account_numbers VARCHAR(30) NOT NULL,
    type VARCHAR(15) NOT NULL,
    created_date DATETIME NOT NULL default(NOW()),
    FOREIGN KEY(account_id) REFERENCES accounts(account_id),
    FOREIGN KEY(account_numbers) REFERENCES accounts(account_numbers)
);

CREATE TABLE account_cancellation_historys(
    account_id INT PRIMARY KEY,
    cancellation_date DATETIME NOT NULL,
    FOREIGN KEY(account_id) REFERENCES accounts(account_id)
);

```