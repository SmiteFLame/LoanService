package com.naverfinancial.creditrating.entity.user.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.naverfinancial.creditrating.entity.user"],
    entityManagerFactoryRef = "userEntityManager",
    transactionManagerRef = "userTransactionManager"
)
class UserConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-user")
    fun userDataSource(): DataSource = DataSourceBuilder.create().build()

    @Bean
    fun userEntityManager(): LocalContainerEntityManagerFactoryBean =
        (LocalContainerEntityManagerFactoryBean()).apply {
            dataSource = userDataSource()
            setPackagesToScan("com.naverfinancial.creditrating.entity.user")
            jpaVendorAdapter = HibernateJpaVendorAdapter()
        }

    @Bean
    fun userManager() = JpaTransactionManager(userEntityManager().`object`!!)
}