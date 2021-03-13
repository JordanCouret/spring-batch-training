package fr.rha.jco.batch.batch;

import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import fr.rha.jco.batch.input.StudentCsv;
import fr.rha.jco.batch.listener.Ex3Listener;
import fr.rha.jco.batch.output.StudentBdd;
import fr.rha.jco.batch.processor.Ex1Processor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DuplicateKeyException;

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
	public Step ex4Step(@Qualifier("ex4Reader") ItemReader<StudentCsv> reader,
						final Ex1Processor processor,
						final Ex3Listener skipListener,
						@Qualifier("ex4Writer") ItemWriter<StudentBdd> writer) {
		return this.stepFactory.get("ex4Step")
				.<StudentCsv, StudentBdd>chunk(10)
				.faultTolerant()
				.skip(ValidationException.class)
				.skip(FlatFileParseException.class)
				.skip(DuplicateKeyException.class)
				.skipLimit(Integer.MAX_VALUE)
				.listener(skipListener)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}

	@Bean
	@StepScope
	public JdbcCursorItemReader<StudentCsv> ex4Reader(@Qualifier("oneDataSource") DataSource dataSource) {
		return new JdbcCursorItemReaderBuilder<StudentCsv>()
				.name("ex4Reader")
				.dataSource(dataSource)
				.sql("SELECT id, firstName, lastName, address, birth FROM student")
				.rowMapper((rs, i) -> StudentCsv.builder()
						.id(rs.getLong(1))
						.firstName(rs.getString(2))
						.lastName(rs.getString(3))
						.address(rs.getString(4))
						.birth(rs.getDate(5).toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
						.build())
				.fetchSize(10)
				.maxRows(100)
				.build();
	}

	@Bean
	@StepScope
	public CompositeItemWriter<StudentBdd> ex4Writer(@Qualifier("ex4BddWriter") JdbcBatchItemWriter<StudentBdd> ex4BddWriter,
													@Qualifier("ex4FileWriter") FlatFileItemWriter<StudentBdd> ex4FileWriter) {
		return new CompositeItemWriterBuilder<StudentBdd>()
				.delegates(ex4BddWriter, ex4FileWriter)
				.build();
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<StudentBdd> ex4BddWriter(@Qualifier("twoDataSource") DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<StudentBdd>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO student (id, firstName, lastName, address, birth) VALUES (:id, :firstName, :lastName, :address, :birth)")
				.dataSource(dataSource)
				.build();
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<StudentBdd> ex4FileWriter() {
		return new FlatFileItemWriterBuilder<StudentBdd>()
				.name("ex4FileWriter")
				.resource(this.ackResource)
				.lineAggregator(new DelimitedLineAggregator<>() {
					{
						setDelimiter(",");
						setFieldExtractor(new BeanWrapperFieldExtractor<>() {
							{
								setNames(new String[] { "id", "firstName", "lastName", "address", "birth" });
							}
						});
					}
				})
				.build();
	}
}
