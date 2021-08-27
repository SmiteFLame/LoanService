package com.naverfinancial.creditrating.entity.creditRatingSearch.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import javax.persistence.EntityManagerFactory

class CreditRatingSearchJpaTransactionManager : JpaTransactionManager(){
    @Qualifier("creditRatingSearchLocalContainerEntityManagerFactoryBean")
    @Autowired
    lateinit var creditRatingSearchEntityManager: LocalContainerEntityManagerFactoryBean

    override fun getEntityManagerFactory(): EntityManagerFactory? {
        return creditRatingSearchEntityManager.`object`
    }
}
