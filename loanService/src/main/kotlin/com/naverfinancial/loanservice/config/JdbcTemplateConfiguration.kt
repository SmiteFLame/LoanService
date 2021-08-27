package com.naverfinancial.loanservice.config


import com.naverfinancial.loanservice.config.entityConfig.AccountJpaTransactionManager
import com.naverfinancial.loanservice.config.entityConfig.UserJpaTransactionManager
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

    @Qualifier("accountTransactionManager")
    @Bean
    fun accountTransactionManager(accountDataSource: DataSource?): PlatformTransactionManager? {
        val jpaTransactionManager = AccountJpaTransactionManager()
        jpaTransactionManager.dataSource = accountDataSource
        return jpaTransactionManager
    }

}
