package com.naverfinancial.loanservice.config

import com.naverfinancial.loanservice.entity.user.config.UserJpaTransactionManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
        val jpaTransactionManager = UserJpaTransactionManager()
        jpaTransactionManager.dataSource = userDataSource
        return jpaTransactionManager
    }

    @Qualifier("account")
    @Bean
    fun accountTransactionManager(accountDataSource: DataSource?): PlatformTransactionManager? {
        val jpaTransactionManager = JpaTransactionManager()
        jpaTransactionManager.dataSource = accountDataSource
        return jpaTransactionManager
    }

}