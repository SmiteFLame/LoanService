package com.naverfinancial.loanservice.wrapper

class Register {
    private var emails: String
    private var user_name: String
    private var age: Int
    private var salary: Int

    constructor(emails: String, user_name: String, age: Int, salary: Int) {
        this.emails = emails
        this.user_name = user_name
        this.age = age
        this.salary = salary
    }
    fun getEmails() = emails
    fun getUserName() = user_name
    fun getAge() = age
    fun getSalary() = salary
}