package fr.rha.jco.batch.integration;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import fr.rha.jco.batch.batch.Ex4Configuration;
import fr.rha.jco.batch.service.RetryService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/ex1.sql")
@SpringBootTest
@ActiveProfiles("ex5-retry")
class Ex5Tests {
	@Autowired
	@Qualifier("ex1JobTest")
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private RetryService retryService;

	@Test
	@DisplayName("Should handle a job retry")
	@SneakyThrows
	void shouldSucess() {
		// GIVEN
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addString("fileName", new ClassPathResource("ex5.csv").getFile().getAbsolutePath());

		// Should failed cause address is too long
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParametersBuilder.toJobParameters());
		assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("FAILED");
		assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM student", Integer.class)).isEqualTo(0);

		// Update H2 varchar to handle this error
		jdbcTemplate.update("alter table student drop column address");
		jdbcTemplate.update("alter table student add address varchar(400)");

		JobExecution retryExecution = retryService.retry(jobExecution);
		assertThat(retryExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
		assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM student", Integer.class)).isEqualTo(3);
	}
}
