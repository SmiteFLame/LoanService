package com.naverfinancial.creditrating.entity.creditRatingSearch.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import javax.persistence.EntityManagerFactory

class CreditRatingSearchJpaTransactionManager : JpaTransactionManager(){
    @Qualifier("creditRatingSearchEntityManager")
    @Autowired
    lateinit var creditRatingSearchEntityManagerFactoryBean: LocalContainerEntityManagerFactoryBean

    override fun getEntityManagerFactory(): EntityManagerFactory? {
        return creditRatingSearchEntityManagerFactoryBean.`object`
    }

}