package com.naverfinancial.loanservice.entity.account.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import javax.persistence.EntityManagerFactory

class AccountJpaTransactionManager : JpaTransactionManager() {
    @Qualifier("accountLocalContainerEntityManagerFactoryBean")
    @Autowired
    lateinit var accountLocalContainerEntityManagerFactoryBean: LocalContainerEntityManagerFactoryBean

    override fun getEntityManagerFactory(): EntityManagerFactory? {
        return accountLocalContainerEntityManagerFactoryBean.`object`
    }
}
