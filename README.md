# 202108-internship-loan-system

## REST 명세서
![REST명세서](./image/REST명세서.png)

## 플로우 차트 및 유스케이스 명세서

### 유저 정보 조회

![유저정보조회](./image/flow/UC1,2.png)

### UC1 : 유저 정보 전체 조회

### 메인 시나리오
```
1. 사용자로 부터 Page 조건을 입력 받는다.
2. 유저의 정보를 데이터베이스에서 확인한다.
3. 유저의 정보와 200(OK) 상태를 전송한다.
```

### 서브 시나리오
```
2a. 해당하는 유저의 정보가 존재하지 않을 경우
    1. "사용자가 존재하지 않습니다" 메시지를 저장한다.
    2. 404(NOT_FOUND) 상태를 전송한다.
```

### UC2 : NDI 유저 정보 조회

### 메인 시나리오
```
1. 사용자로 부터 검색 조건을 입력 받는다.
2. 유저의 정보를 데이터베이스에서 확인한다.
3. 유저의 정보와 200(OK) 상태를 전송한다.
```

### 서브 시나리오
```
1a. id-type입력값이 "emai"인 경우
    1. Pathvariable값을 "email"로 확인하여 데이터베이스에서 조회한다.
1b. id-type입력값이 없는 경우
    1. "존재하지 않는 IdType입니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
1c. id-type입력값이 없는 경우
    1. Pathvariable값을 "ndi"로 확인하여 데이터베이스에서 조회한다.
2a. 해당하는 유저의 정보가 존재하지 않을 경우
    1. "사용자가 존재하지 않습니다" 메시지를 저장한다.
    2. 404(NOT_FOUND) 상태를 전송한다.
```

### 유저 정보 추가, 신용 등급 조회
![신용등급조회](./image/flow/UC3,4.png)

### UC3 : 유저 정보 추가

### 메인 시나리오
```
1. 사용자로 부터 유저 정보를 입력 받는다.
2. 이메일 중복검사를 통해 아이디가 존재한지 확인한다.
3. 유저의 정보를 데이터베이스에서 입력한다.
4. 유저의 정보와 200(OK) 상태를 전송한다.
```

### 서브 시나리오
```
1a. RequestBody가 없는 경우
    1. "입력값이 존재하지 않습니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
1b. RequestBody가 일부 없는 경우
    1. "입력값이 잘못 들어왔습니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
1c. 이메일 조건이 성립되지 않는 경우
    1. "이메일 조건이 정확하지 않습니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
2a. 이미 가입된 이메일이 존재하는 경우
    1. "이메일 조건이 정확하지 않습니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
```
### UC4 : 신용등급 조회

### 메인 시나리오
```
1. 사용자로 부터 NDI을 입력받는다.
2. 신용평가 모듈에 신용등급 조회를 요청한다.
3. 유저의 정보가 존재한지 확인한다.
4. CB 모듈에게 신용등급 조회를 요청한다.
5. 신용동급을 바탕으로 대출 가능 여부를 판단한다.
6. 신용평가 조회 기록과 결과를 신용평가 데이터베이스에 저장한다.
7. 신용평가 모듈에서 대출 서비스 모듈로 결과값을 전달한다.
8. 사용자에게 신용평가 결과 값과 201(CREATED) 상태를 전송한다.
```

### 서브 시나리오
```
2a. 이미 검색된 신용 정보가 존재하는 경우
    1. 신용등급과 결과를 전송한다.
    2. 200(OK) 상태를 전송한다.
2b. 신용평가 모듈에 접근할 수 없는 경우
    1. "신용 등급 서버가 열리지 않았습니다" 메시지를 저장한다.
    2. 500(INTERNAL_SERVER_ERROR) 상태를 전송한다.
3a. 사용자의 유저 정보가 없는 경우
    1. "사용자가 존재하지 않습니다" 메시지를 저장한다.
    2. 404(NOT_FOUND) 상태를 전송한다.
4a. CB서버에 접근할 수 없는 경우
    1. "CB서버가 열리지 않았습니다" 메시지를 저장한다.
    2. 500(INTERNAL_SERVER_ERROR) 상태를 전송한다.
4b. 조회가 10초 이상 걸리는 경우
    1. "CB서버의 제한시간이 초과되었습니다" 메시지를 저장한다.
    2. 504(GATEWAY_TIMEOUT) 상태를 전송한다.
```

### 계좌 정보 조회
![계좌정보조회](./image/flow/UC5,6.png)

### UC5 : 계좌 정보 전체 조회

### 메인 시나리오
```
1. 사용자로 부터 Page 조건과 검색 조건을 입력 받는다.
2. 유저의 정보를 데이터베이스에서 확인한다.
3. 유저의 정보와 200(OK) 상태를 전송한다.
```

### 서브 시나리오
```
1a. "ndi" 값이 입력된 경우
    1. RequestParam ndi를 확인한다.
        1a. RequestParam ndi가 없는 경우
            1. "계좌가 존재하지 않습니다" 메시지를 저장한다.
            2. 404(NOT_FOUND) 상태를 전송한다.
    2. 유저의 정보를 NDI기준으로 검색한다.
1b. "ndi" 값이 없는 경우
    1. 유저의 정보를 조건 없이 검색한다.
2a. 해당하는 계좌 정보가 존재하지 않을 경우
    1. "계좌가 존재하지 않습니다" 메시지를 저장한다.
    2. 404(NOT_FOUND) 상태를 전송한다.
```

### UC6 : 계좌 아이디 별 계좌 정보 전체 조회

### 메인 시나리오
```
1. 사용자로 부터 계좌 아이디를 입력 받는다.
2. 유저의 정보를 데이터베이스에서 확인한다.
3. 유저의 정보와 200(OK) 상태를 전송한다.
```

### 서브 시나리오
```
2a. 계좌 정보가 없는 경우
    1. "계좌가 존재하지 않습니다" 메시지를 저장한다.
    2. 404(NOT_FOUND) 상태를 전송한다.
```

### 마이너스 통장 신청
![마이너스통장신청](./image/flow/UC7.png)

### UC7 : 마이너스 계좌 신청

### 메인 시나리오
```
1. 사용자로 부터 NDI를 입력 받는다.
2. NDI를 통해서 유저 정보를 확인한다.
3. 계좌 아이디를 통해서 계좌 정보를 확인한다.
4. 신용 등급 조회 결과가 존재한지 확인한다.
5. 대출 가능 여부를 확인한다.
6. 새로운 통장을 데이터베이스에 저장한다.
7. 통장 정보와 201(CREATED) 상태를 전송한다.
```

### 서브 시나리오
```
1a. 입력값이 들어오지 않는 경우
    1. "입력값이 잘못 들어왔습니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
1b. NDI이 잘못 들어온 경우
    1. "NDI 입력값이 잘못되었습니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
2a. 사용자 정보가 없는 경우
    1. "사용자가 존재하지 않습니다" 메시지를 저장한다.
    2. 404(NOT_FOUND) 상태를 전송한다.
3a. 이미 계좌 정보가 존재하는 경우
    1. "이미 계좌를 가지고 있습니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
4a. 신용 등급 조회 결과가 없는 경우
    1. "신용등급이 존재하지 않습니다" 메시지를 저장한다.
    2. 404(NOT_FOUND) 상태를 전송한다.
5a. 대출 조건성립 되지 않은 경우
    1. "신용등급 미달로 대출 신청이 불가능합니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
```

### 대출 신청 및 반환
![대출신청및반환](./image/flow/UC8.png)

### UC8 : 대출 신청 및 반환

### 메인 시나리오
```
1. 사용자로 부터 계좌 아이디와 신청 금액를 입력 받는다.
2. 계좌 아이디를 통해서 계좌 정보를 확인한다.
3. 요구된 상황에 맞춰서 대출 신청 혹은 반환을 한다.
4. 통장 정보와 201(CREATED) 상태를 전송한다.
```

### 서브 시나리오
```
1a. RequestBody 입력값이 들어오지 않는 경우
    1. "입력값이 존재하지 않습니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
1a. RequestBody가 일부 없는 경우
    1. "입력값이 잘못 들어왔습니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
1c. 신청 금액이 0 혹은 음수 인경우
    1. "잘못된 금액 신청입니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
2a. 계좌 정보가 없는 경우
    1. "계좌가 존재하지 않습니다" 메시지를 저장한다.
    2. 404(NOT_FOUND) 상태를 전송한다.
2b. 계좌가 이미 해지된 상태 인 경우
    1. "이미 해지된 계좌입니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
3a. 대출 신청인 경우
    1. 대출 신청하면 한도를 초과하는지 확인한다.
        1a. 한도를 초과하는 경우
            1. "대출 한도를 초과하였습니다" 메시지를 저장한다.
            2. 200(OK) 상태를 전송한다.
    2. 대출 신청 후 계좌를 데이터베이스에 저장한다.
3b. 대출 반환인 경우
    1. 대출 반환 후 계좌를 데이터베이스에 저장한다.
    2. 현재 통장 상태를 확인한다.
        2a. 마이너스 통장인 경우
            1. 통장에 반환 금액을 넣는다.
                1a. 반환 금액을 넣은후 잔액이 양수가 아닌 경우
                    1. 반환 기록을 데이터베이스에 저장 한다.
                1b. 반환 금액을 넣은후 잔액이 양수인 경우
                    1. 잔액을 제외한 반환 금액을 반환 기록을 베이터베이스에 저장한다.
        2b. 마이너스 통장이 아닌 경우
            1. 반환 기록 없이 진행한다.
3c. 잘못 된 요구사항인 경우
    1. "입력값이 잘못 들어왔습니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
```

### 거래 내역 조회
![거래내역조회](./image/flow/UC9.png)

### UC9 : 거래 내역 전체 조회

### 메인 시나리오
```
1. 사용자로 부터 Page 조건과 검색 조건을 입력 받는다.
2. 계좌 거래 기록을 데이터베이스에서 확인한다.
3. 유저의 정보와 200(OK) 상태를 전송한다.
```

### 서브 시나리오
```
1a. "account-id" 값이 입력된 경우
    1. RequestParam account-id를 확인한다.
        1a. RequestParam ndi가 없는 경우
            1. "존재하지 않는 IdType입니다" 메시지를 저장한다.
            2. 400(BAD_REQUEST) 상태를 전송한다.
    2. 계좌 정보를 검색한다.
        2a. 해당하는 계좌 정보가 존재하지 않을 경우
            1. "계좌가 존재하지 않습니다" 메시지를 저장한다.
            2. 404(NOT_FOUND) 상태를 전송한다.
1b. "account-id" 값이 없는 경우
    1. 계좌 거래 기록을 조건 없이 검색한다.
2a. 계좌 거래 내역이 없는 경우
    1. "계좌거래내역이 존재하지 않습니다" 메시지를 저장한다.
    2. 404(NOT_FOUND) 상태를 전송한다.
```

### 통장 해지
![통장해지](./image/flow/UC10.png)
### UC12 : 마이너스 계좌 해지

### 메인 시나리오
```
1. 사용자로 부터 계좌 아이디를 입력 받는다.
2. 계좌 정보를 확인한다.
3. 통장의 잔고 상태를 확인한다.
4. 통장을 해지 상태로 변경하고 데이터베이스에 저장한다.
5. 남은 잔고와 200(OK) 상태를 전송한다.
```

### 서브 시나리오
```
2a. 계좌 정보가 없는 경우
    1. "계좌가 존재하지 않습니다" 메시지를 저장한다.
    2. 404(NOT_FOUND) 상태를 전송한다.
3a. 이미 계좌가 해지된 상태 인 경우
    1. "이미 해지된 계좌입니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
4a. 통장 잔고가 마이너스인 경우
    1. "잔고가 0이 아닙니다" 메시지를 저장한다.
    2. 400(BAD_REQUEST) 상태를 전송한다.
```


## 데이터베이스

### E-R 다이어그램
![REST명세서](./image/ERD.png)

### UserDB

1. Docker
``` Terminal
docker run -p 53306:3306 --name UserDB -e MYSQL_ROOT_PASSWORD=naver -e MYSQL_DATABASE="user" -d mysql:latest --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

2. SQL
```SQL
CREATE TABLE user(
    NDI VARCHAR(36) PRIMARY KEY,
    email VARCHAR(50) not null unique,
    user_name VARCHAR(10) not null,
    age INT NOT NULL,
    salary INT NOT NULL
);

CREATE TABLE user_credit_rating(
    NDI VARCHAR(36) PRIMARY KEY,
    grade INT NOT NULL,
    isPermit BOOLEAN NOT NULL,
    created_date DATETIME NOT NULL default(NOW()),
    FOREIGN KEY(NDI) REFERENCES user(NDI)
);
```

### CreditRatingSearchDB

1. Docker
``` Terminal
docker run -p 63306:3306 --name CreditRatingSearchDB -e MYSQL_ROOT_PASSWORD=naver -e MYSQL_DATABASE="credit_rating_search" -d mysql:latest --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

2. SQL
```SQL
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

```
### AccountDB

1. Docker
``` Terminal
docker run -p 43306:3306 --name AccountDB -e MYSQL_ROOT_PASSWORD=naver -e MYSQL_DATABASE="account" -d mysql:latest --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```
2. SQL
```SQL
CREATE TABLE account(
    account_id INT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    NDI VARCHAR(36) NOT NULL,
    loan_limit INT NOT NULL,
    balance INT NOT NULL,
    grade INT NOT NULL,
    status VARCHAR(15) NOT NULL default("normal"),
    created_date DATETIME NOT NULL default(NOW()),
    cancelled_date DATETIME
);

ALTER TABLE account ADD INDEX (account_number);

CREATE TABLE account_transaction_history(
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    account_id INT NOT NULL,
    account_number VARCHAR(30) NOT NULL,
    amount INT NOT NULL,
    type VARCHAR(15) NOT NULL,
    translated_date DATETIME NOT NULL default(NOW()),
    FOREIGN KEY(account_id) REFERENCES account(account_id),
    FOREIGN KEY(account_number) REFERENCES account(account_number)
);

CREATE TABLE account_cancellation_history(
    account_id INT PRIMARY KEY,
    cancellation_date DATETIME NOT NULL,
    FOREIGN KEY(account_id) REFERENCES account(account_id)
);

```

## 실행방법

1. Docker를 통해서 서버 3개 실행

2. CB 모듈 실행
- [CB모듈](https://oss.navercorp.com/finance-css/intern-dummy-cb)

3. creditRating Spring Boot 실행

4. loanService Spring Boot 실행

## 부하 테스트

## 에러테스트
- 조건: 데이터베이스 Timeout: 10초
- Retry 회수 제한 - 2번 가정
- 오차율 = (예상 실패 건수 - 실제 실패 건수) / 예상 실패 건수 * 100 (%)
### TC1
- 에러율 50%, 딜레이 3 ~ 15초
- 1차 타임 아웃 경우의 수 240 / 576
- 1차 성공 경우의 수 168 / 576
- 2차 타임 아웃 경우의 수 35 / 576
- 2차 성공 경우의 수 49 / 576
- 실패 경우의 수 84 / 576

|테스트 번호|시도 횟수|캐시 개수|데이터 개수|테스트케이스|동시접속수|Connection Pool 개수|예상 실패 건수|실제 실패 건수|오차율|(실패)데이터베이스 시간 초과|(실패) CB 에러 수|비고|
|---|---|---|---|---|---|---|---|---|---|---|---|---|
|1|1번|0개|1개|100개|100개|200개|약 47개|88개|약 -87%|0개|10개|기본 테스트|
|2|1번|0개|1개|2000개|1개|200개|약 1416개|장시간으로 포기||장시간으로 포기|장시간으로 포기|동시접속 X 변경| 
|3|1번|0개|1개|2000개|10개|200개|약 1416개|장시간으로 포기||장시간으로 포기|장시간으로 포기|동시접속 10개 변경|
|4|1번|0개|1개|2000개|100개|200개|약 1416개|1833개|약 -29%|1666개|167개|동시접속 100개|
|5-1|1번|0개|1개|2000개|100개|Default(10개)|약 1416개|1829개|약 -29%|1667개|162개|커넥션 Pool 기본값인 경우|
|5-2|1번|0개|1개|2000개|100개|Default(10개)|약 1416개|1845개|약 -29%|1690개|155개|커넥션 Pool 기본값인 경우|
|6|1번|0개|1개|2000개|200개|200개|약 1416개|1905개|약 -35%|1821개|84개|동시접속 200개 변경|
|7-1|2번|0개|1개|2000개|200개|200개|약 1246개|1830개|약 -47%|1752개|78개|Retry 2번 추가|
|7-2|2번|0개|1개|2000개|200개|200개|약 1246개|1826개|약 -47%|1756개|70개|Retry 2번 추가|
|8-1|2번|3개|5개|2000개 * 5|200개 * 5|200개|???|7개||0개|7개|캐시 3개 및 데이터 개수 5개 동시 실행|
|8-2|2번|3개|5개|2000개 * 5|200개 * 5|200개|???|102개||75개|27개|캐시 3개 및 데이터 개수 5개 동시 실행|
|9|2번|3개|5개|2000개 * 5|200개 * 5|Default(10개)|???|2개||0개|2개|캐시 동기화 기능 추가(LinkedList)|
|10|1번|0개|1개|2000개|200개|200개|약 1416개|1534개|약 -8%|???|???|트랜잭션 완전 제거|
|11|1번|0개|1개|2000개|200개|200개|약 1416개|1159개|약 18%|365개|794개|저장 부분만 트랜잭션으로 변경|
|12|2번|0개|1개|2000개|200개|200개|약 1246개|641개|약 49%|112개|529개|저장 부분만 트랜잭션으로 변경 및 Retry 2번|
|13|2번|0개|1개|2000개|200개|200개|약 1246개|1108개|약 11%|248개|860개|저장 부분에 Lock있는 경우|


### 트랜잭션 분리 후 결과 비교
|테스트 번호|시도 횟수|캐시 개수|데이터 개수|테스트케이스|동시접속수|Connection Pool 개수|예상 실패 건수|실제 실패 건수|오차율|(실패)데이터베이스 시간 초과|(실패) CB 에러 수|비고|
|---|---|---|---|---|---|---|---|---|---|---|---|---|
|5-1|1번|0개|1개|2000개|100개|Default(10개)|약 1416개|1829개|약 -29%|1667개|162개|트랜잭션 분리 전|
|5-2|1번|0개|1개|2000개|100개|Default(10개)|약 1416개|1845개|약 -29%|1690개|155개|트랜잭션 분리 전|
|14-1|1번|0개|1개|2000개|100개|Default(10개)|약 1416개|1149개|약 19%|329개|820개|트랜잭션 완전 제거|
|14-2|1번|0개|1개|2000개|100개|Default(10개)|약 1416개|1136개|약 20%|246개|890개|트랜잭션 완전 제거|
|15-1|1번|3개|5개|2000개|100개|Default(10개)|약 1416개|293개|약 79%|87개|204개|트랜잭션 완전 제거|
|15-2|1번|3개|5개|2000개|100개|Default(10개)|약 1416개|483개|약 66%|128개|355개|트랜잭션 완전 제거|
|16-1|2번|3개|5개|2000개|100개|Default(10개)|약 1246개|491개|약 79%|140개|349개|트랜잭션 완전 제거|
|16-2|2번|3개|5개|2000개|100개|Default(10개)|약 1246개|479개|약 65%|116개|363개|트랜잭션 완전 제거|

트랜잭션 분리
- 트랜잭션을 분리하였더니 예상 성능보다 훨씬 더 좋은 성능이 나옴
- 데이터베이스 시간 초과는 매우 줄었으나 그 만큼 CB에러 확률이 증가
- 데이터베이스 시간 초과는 약 833개로 예상하였으나 약 300개로 줄어서 잘못쓴 코드 다시 한번 찾기
캐시
- 캐시를 사용하여 오류를 최대한 감소
- 캐시를 사용하면 Retry의 수와 상관 없이 에러개수거 비슷함 완료됨..?

에러
- 두가지의 추가 오류 발생 (java.io.IOException)
    - chunked transfer encoding, state: READING_LENGTH
        - LoanService의 ```searchGrade``` 함수
        -  ```val response = client.send(request, HttpResponse.BodyHandlers.ofString())``` 위치에서 발생이 되는중
    - connection closed locally
        - 위치 파악중

### TC2
- 에러율 20%, 딜레이 0 ~ 2초
- 1차 타임 아웃 경우의 수 0 / 25
- 1차 성공 경우의 수 20 / 25
- 2차 타임 아웃 경우의 수 0 / 25
- 2차 성공 경우의 수 4 / 25
- 실패 경우의 수 1 / 25


|테스트 번호|시도 횟수|캐시 개수|데이터 개수|테스트케이스|동시접속수|Connection Pool 개수|예상 실패 건수|실제 실패 건수|오차율|(실패)데이터베이스 시간 초과|(실패) CB 에러 수|비고|
|---|---|---|---|---|---|---|---|---|---|---|---|---|
|1|1번|0개|1개|100개|100개|200개|20개|16개|20%|0개|16개|기본 테스트|
|2|1번|0개|1개|2000개|1개|400개|200개|401개|-101%|0개|401개|동시접속 X 변경| 
|3|1번|0개|1개|2000개|10개|400개|200개|387개|-93.5%|0개|387개|동시접속 10개 변경|
|4-1|1번|0개|1개|2000개|100개|200개|400개|362개|9.5%|0개|362개|동시접속 100개|
|4-2|1번|0개|1개|2000개|100개|200개|400개|404개|-1%|개|404개|동시접속 100개|
|5-1|1번|0개|1개|2000개|100개|Default(10개)|400개|541개|-35.25%|182개|360개|커넥션 Pool 기본값인 경우|
|5-2|1번|0개|1개|2000개|100개|Default(10개)|400개|408개|-2%|28개|380개|커넥션 Pool 기본값인 경우|
|5-3|1번|0개|1개|2000개|100개|Default(10개)|400개|471개|-17.5%|108개|363개|커넥션 Pool 기본값인 경우|
|6-1|1번|0개|1개|2000개|200개|200개|400개|436개|-9%|76개|360개|동시접속 200개 변경|
|6-2|1번|0개|1개|2000개|200개|200개|400개|698개|-74.5%|357개|341개|동시접속 200개 변경|
|7-1|2번|0개|1개|2000개|200개|200개|80개|168개|-110%|60개|108개|Retry 2번 추가|
|7-2|2번|0개|1개|2000개|200개|200개|80개|145개|-81.25%|54개|91개|Retry 2번 추가|
|8-1|2번|3개|5개|2000개 * 5|200개 * 5|200개|???|18개||0개|18개|캐시 3개 및 데이터 개수 5개 동시 실행|
|8-2|2번|3개|5개|2000개 * 5|200개 * 5|200개|???|9개||0개|9개|캐시 3개 및 데이터 개수 5개 동시 실행|
|8-3|2번|3개|5개|2000개 * 5|200개 * 5|200개|???|24개||0개|24개|캐시 3개 및 데이터 개수 5개 동시 실행|
|9|2번|3개|5개|2000개 * 5|200개 * 5|Default(10개)|???|9600개||0개|0개|캐시 동기화 기능 없는 경우|
|10|2번|3개|5개|2000개 * 5|200개 * 5|Default(10개)|???|27개||0개|27개|캐시 동기화 기능 추가|

