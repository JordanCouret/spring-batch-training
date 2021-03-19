package fr.rha.jco.batch.batch;

import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import fr.rha.jco.batch.input.StudentCsv;
import fr.rha.jco.batch.output.StudentBdd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class Ex4Configuration {
	public static final String FILE_NAME = "ack_file.csv";

	public final Resource ackResource = new ClassPathResource(FILE_NAME);

	private final JobBuilderFactory jobFactory;

	private final StepBuilderFactory stepFactory;

	@Bean
	public Job ex4Job(@Qualifier("ex4Step") Step step) {
		return this.jobFactory.get("ex4Job")
				.start(step)
				.build();
	}

	@Bean
	public Step ex4Step() {
		return this.stepFactory.get("ex4Step")
				.<StudentCsv, StudentBdd>chunk(10)
				// TODO Ex 4
				.reader(() -> StudentCsv.builder().id(1L).build())
				.writer(items -> items.forEach(studentBdd -> log.info("{}", studentBdd.toString())))
				.build();
	}
}
