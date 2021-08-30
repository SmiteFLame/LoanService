package com.naverfinancial.loanservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.naverfinancial.loanservice.datasource.account"],
    entityManagerFactoryRef = "accountEntityManager",
    transactionManagerRef = "accountTransactionManager"
)
class AccountConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-account")
    fun accountDataSource(): DataSource = DataSourceBuilder.create().build()

    @Bean
    fun accountEntityManager(): LocalContainerEntityManagerFactoryBean =
        (LocalContainerEntityManagerFactoryBean()).apply {
            dataSource = accountDataSource()
            setPackagesToScan("com.naverfinancial.loanservice.datasource.account")
            jpaVendorAdapter = HibernateJpaVendorAdapter()
        }

    @Bean
    fun accountTransactionManager(accountDataSource: DataSource?): PlatformTransactionManager? {
        val jpaTransactionManager = JpaTransactionManager()
        jpaTransactionManager.entityManagerFactory = accountEntityManager().`object`
        jpaTransactionManager.dataSource = accountDataSource
        return jpaTransactionManager
    }
}
