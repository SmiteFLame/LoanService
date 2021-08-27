package com.naverfinancial.creditrating.config.entityConfig

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.naverfinancial.creditrating.entity.creditRatingSearch"],
    entityManagerFactoryRef = "creditRatingSearchEntityManager",
    transactionManagerRef = "creditRatingSearchTransactionManager"
)
class CreditRatingSearchConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-credit-rating-search")
    fun creditRatingSearchDataSource(): DataSource = DataSourceBuilder.create().build()

    @Primary
    @Qualifier("creditRatingSearchLocalContainerEntityManagerFactoryBean")
    @Bean
    fun creditRatingSearchEntityManager(): LocalContainerEntityManagerFactoryBean =
        (LocalContainerEntityManagerFactoryBean()).apply {
            dataSource = creditRatingSearchDataSource()
            setPackagesToScan("com.naverfinancial.creditrating.entity.creditRatingSearch")
            jpaVendorAdapter = HibernateJpaVendorAdapter()
        }
}


class CreditRatingSearchJpaTransactionManager : JpaTransactionManager(){
    @Qualifier("creditRatingSearchLocalContainerEntityManagerFactoryBean")
    @Autowired
    lateinit var creditRatingSearchEntityManager: LocalContainerEntityManagerFactoryBean

    override fun getEntityManagerFactory(): EntityManagerFactory? {
        return creditRatingSearchEntityManager.`object`
    }
}

