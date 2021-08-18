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
```