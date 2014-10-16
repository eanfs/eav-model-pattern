package pl.softech.eav;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import pl.softech.eav.domain.ComputerModelInitializationService;
import pl.softech.eav.domain.PersonModelInitializationService;
import pl.softech.eav.domain.attribute.AttributeRepository;
import pl.softech.eav.domain.attribute.DataTypeSerialisationService;
import pl.softech.eav.domain.dictionary.DictionaryEntryRepository;
import pl.softech.eav.domain.frame.FrameFactory;
import pl.softech.eav.domain.relation.RelationConfigurationRepository;
import pl.softech.eav.infrastructure.jpa.TableNamingStrategy;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author ssledz
 */
@Configuration
@EnableJpaRepositories("pl.softech.eav.domain")
public class MySqlConfig {

	@Bean
	public DataSource dataSource() {

		ComboPooledDataSource cpds = new ComboPooledDataSource();
		cpds.setJdbcUrl("jdbc:mysql://localhost:3306/sample?createDatabaseIfNotExist=true&sessionVariables=sql_mode=NO_BACKSLASH_ESCAPES&useUnicode=yes&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8_unicode_ci");
		cpds.setUser("test");
		cpds.setPassword("test");
		return cpds;
	}

	@Bean
	public FrameFactory frameFactory(AttributeRepository attributeRepository,
			RelationConfigurationRepository relationConfigurationRepository) {
		return new FrameFactory(attributeRepository, relationConfigurationRepository);
	}

	@Bean
	public DataTypeSerialisationService dataTypeSerialisationService(DictionaryEntryRepository dictionaryEntryRepository) {
		return new DataTypeSerialisationService(dictionaryEntryRepository);
	}

	@Bean
	public ComputerModelInitializationService computerModelInitializationService() {
		return new ComputerModelInitializationService();
	}

	@Bean
	public PersonModelInitializationService personModelInitializationService() {
		return new PersonModelInitializationService();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean lemfb = new LocalContainerEntityManagerFactoryBean();
		lemfb.setDataSource(dataSource());
		lemfb.setJpaVendorAdapter(jpaVendorAdapter());
		lemfb.setPackagesToScan("pl.softech.eav.domain");
		lemfb.setMappingResources("named-queries.xml");
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.ejb.naming_strategy", TableNamingStrategy.class.getName());
		jpaProperties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
		jpaProperties.setProperty("hibernate.cache.use_second_level_cache", "true");
		jpaProperties.setProperty("hibernate.show_sql", "true");
		jpaProperties.setProperty("hibernate.format_sql", "true");
		lemfb.setJpaProperties(jpaProperties);
		return lemfb;
	}

	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setDatabase(Database.MYSQL);
		jpaVendorAdapter.setGenerateDdl(true);
		return jpaVendorAdapter;
	}

}