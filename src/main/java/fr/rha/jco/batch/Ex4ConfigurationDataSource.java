package fr.rha.jco.batch;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Profile("ex4")
public class Ex4ConfigurationDataSource {
	@Primary
	@BatchDataSource
	@Bean
	@ConfigurationProperties("spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean
	@ConfigurationProperties("one.datasource")
	public DataSource oneDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean
	@ConfigurationProperties("two.datasource")
	public DataSource twoDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean
	public JdbcTemplate oneJdbcTemplate() {
		return new JdbcTemplate(this.oneDataSource());
	}

	@Bean
	public JdbcTemplate twoJdbcTemplate() {
		return new JdbcTemplate(this.twoDataSource());
	}
}
