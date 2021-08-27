package com.naverfinancial.creditrating.config

import com.naverfinancial.creditrating.entity.creditRatingSearch.config.CreditRatingSearchJpaTransactionManager
import com.naverfinancial.creditrating.entity.user.config.UserJpaTransactionManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
class JdbcTemplateConfiguration {

    @Qualifier("userTransactionManager")
    @Bean
    fun userTransactionManager(userDataSource: DataSource?): PlatformTransactionManager? {
        val jpaTransactionManager = UserJpaTransactionManager()
        jpaTransactionManager.dataSource = userDataSource
        return jpaTransactionManager
    }

    @Qualifier("creditRatingSearchTransactionManager")
    @Bean
    fun creditRatingSearchTransactionManager(creditRatingSearchDataSource: DataSource?): PlatformTransactionManager? {
        val jpaTransactionManager = CreditRatingSearchJpaTransactionManager()
        jpaTransactionManager.dataSource = creditRatingSearchDataSource
        return jpaTransactionManager
    }
}
