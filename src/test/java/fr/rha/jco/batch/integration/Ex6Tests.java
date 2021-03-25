package fr.rha.jco.batch.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/ex6_one.sql", config = @SqlConfig(dataSource = "oneDataSource"))
@Sql(scripts = "/ex6_two.sql", config = @SqlConfig(dataSource = "twoDataSource"))
@SpringBootTest
@ActiveProfiles("ex4")
class Ex6Tests {
	@Autowired
	@Qualifier("ex6JobTest")
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	@Qualifier("oneJdbcTemplate")
	private JdbcTemplate oneJdbcTemplate;

	@Autowired
	@Qualifier("twoJdbcTemplate")
	private JdbcTemplate twoJdbcTemplate;

	@Test
	@DisplayName("Should import and handle rollback")
	@SneakyThrows
	@Disabled // A supprimer pour l'Ex 6 (snas les modifs ex 7)
	void shouldSucess() {
		// GIVEN
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addString("fileName", new ClassPathResource("ex6.csv").getFile().getAbsolutePath());

		// WHEN
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParametersBuilder.toJobParameters());

		// THEN
		assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("FAILED");
		assertThat(oneJdbcTemplate.queryForObject("SELECT count(*) FROM student", Long.class))
				.isEqualTo(twoJdbcTemplate.queryForObject("SELECT count(*) FROM address", Long.class))
				.isEqualTo(50);
	}
}
