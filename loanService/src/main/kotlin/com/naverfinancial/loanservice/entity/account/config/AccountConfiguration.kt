package com.naverfinancial.loanservice.entity.account.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.naverfinancial.loanservice.entity.account"],
    entityManagerFactoryRef = "accountEntityManager",
    transactionManagerRef = "accountTransactionManager"
)
class AccountConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-account")
    fun accountDataSource(): DataSource = DataSourceBuilder.create().build()

    @Primary
    @Qualifier("accountEntityManager")
    @Bean
    fun accountEntityManager(): LocalContainerEntityManagerFactoryBean =
        (LocalContainerEntityManagerFactoryBean()).apply {
            dataSource = accountDataSource()
            setPackagesToScan("com.naverfinancial.loanservice.entity.account")
            jpaVendorAdapter = HibernateJpaVendorAdapter()
        }
}