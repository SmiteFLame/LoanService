# 202108-internship-loan-system

## REST 명세서
![REST명세서](./image/REST명세서.png)

## 플로우 차트

### 유저 및 계좌 번호 검색
![유저및계좌번호검색](./image/유저및계좌번호검색.png)

### 마이너스 통장 신청
![마이너스통장신청](./image/마이너스통장신청.png)

### 대출 신청 및 반환, 해지
![대출신청및반환해지](./image/대출신청및반환해지.png)

## 흐름도
### 유저 검색
![유저검색흐름도](./image/유저검색흐름도.png)

### 계좌 번호 검색
![계좌번호검색흐름도](./image/계좌번호검색흐름도.png)

### 마이너스 통장 신청
![마이너스통장신청흐름도](./image/마이너스통장신청흐름도.png)

### 대출 신청 및 반환, 해지
![대출신청및반환해지흐름도](./image/대출신청및반환해지흐름도.png)


## 데이터베이스

### E-R 다이어그램
![REST명세서](./image/ERD.png)

### 데이터베이스 Create 생성문

```SQL

-- UserDB
CREATE TABLE user(
    NDI VARCHAR(36) PRIMARY KEY,
    email VARCHAR(50) not null unique,
    user_name VARCHAR(10) not null,
    age INT NOT NULL,
    salary INT NOT NULL
);

-- CreditDB
CREATE TABLE credit_rating_search_history(
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    NDI VARCHAR(36) NOT NULL,
    grade INT NOT NULL,
    created_date DATETIME NOT NULL default(NOW())
);

CREATE TABLE credit_rating_search_result(
    NDI VARCHAR(36) PRIMARY KEY,
    grade INT NOT NULL,
    history_id INT NOT NULL,
    isPermit BOOLEAN NOT NULL,
    FOREIGN KEY(history_id) references credit_rating_search_history(history_id)
);

-- AccountDB
CREATE TABLE account(
    account_id INT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    NDI VARCHAR(36) NOT NULL,
    loan_limit INT NOT NULL,
    balance INT NOT NULL,
    grade INT NOT NULL,
    status VARCHAR(15) NOT NULL default("normal"),
    created_date DATETIME NOT NULL default(NOW()),
    loan_start_date DATETIME
);

ALTER TABLE accounts ADD INDEX (account_number);

CREATE TABLE account_transaction_history(
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    account_id INT NOT NULL,
    amount INT NOT NULL,
    account_number VARCHAR(30) NOT NULL,
    type VARCHAR(15) NOT NULL,
    created_date DATETIME NOT NULL default(NOW()),
    FOREIGN KEY(account_id) REFERENCES account(account_id),
    FOREIGN KEY(account_numbers) REFERENCES account(account_number)
);

CREATE TABLE account_cancellation_history(
    account_id INT PRIMARY KEY,
    cancellation_date DATETIME NOT NULL,
    FOREIGN KEY(account_id) REFERENCES account(account_id)
);

```