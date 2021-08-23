package com.naverfinancial.creditrating.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
class JdbcTemplateConfiguration {

    @Qualifier("user")
    @Bean
    fun userTransactionManager(userDataSource: DataSource?): PlatformTransactionManager? {
        val jpaTransactionManager = JpaTransactionManager()
        jpaTransactionManager.dataSource = userDataSource
        return jpaTransactionManager
    }

    @Qualifier("account")
    @Bean
    fun creditRatingSearchTransactionManager(creditRatingSearchDataSource: DataSource?): PlatformTransactionManager? {
        val jpaTransactionManager = JpaTransactionManager()
        jpaTransactionManager.dataSource = creditRatingSearchDataSource
        return jpaTransactionManager
    }

}