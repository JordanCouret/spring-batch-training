package fr.rha.jco.batch.integration;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import fr.rha.jco.batch.batch.Ex4Configuration;
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
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("ex4")
@Sql(scripts = "/ex4.sql", config = @SqlConfig(dataSource = "oneDataSource"))
@Sql(scripts = "/ex1.sql", config = @SqlConfig(dataSource = "twoDataSource"))
@SpringBootTest
class Ex4Tests {
	@Autowired
	@Qualifier("ex4JobTest")
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	@Qualifier("twoJdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("Should reader, process and write student in database")
	@SneakyThrows
	void shouldSucess() {
		// GIVEN
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

		// WHEN
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParametersBuilder.toJobParameters());

		// THEN
		assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
		assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM student", Integer.class)).isEqualTo(2);
		try (Reader reader = new InputStreamReader(new ClassPathResource(Ex4Configuration.FILE_NAME).getInputStream(), StandardCharsets.UTF_8)) {
			assertThat(FileCopyUtils.copyToString(reader)).startsWith("1,Jean,Dupont,1,1997-08-29\n"
					+ "4,Jean,Dupont,Other,1997-08-29");
		}

	}
}
