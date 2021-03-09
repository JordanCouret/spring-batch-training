package fr.rha.jco.batch.integration;

import lombok.SneakyThrows;
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
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/ex1.sql")
@SpringBootTest
class Ex2Tests {
	@Autowired
	@Qualifier("ex1JobTest")
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("Should reader, process and write student in database")
	@SneakyThrows
	void shouldSucess() {
		// GIVEN
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addString("fileName", new ClassPathResource("ex2.csv").getFile().getAbsolutePath());

		// WHEN
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParametersBuilder.toJobParameters());

		// THEN
		assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
		assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM student", Integer.class)).isEqualTo(2);
	}
}
