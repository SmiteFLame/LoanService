package com.naverfinancial.loanservice.entity.user.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import javax.persistence.EntityManagerFactory

class UserJpaTransactionManager : JpaTransactionManager() {
    @Qualifier("userLocalContainerEntityManagerFactoryBean")
    @Autowired
    lateinit var userLocalContainerEntityManagerFactoryBean: LocalContainerEntityManagerFactoryBean

    override fun getEntityManagerFactory(): EntityManagerFactory? {
        return userLocalContainerEntityManagerFactoryBean.`object`
    }
}
