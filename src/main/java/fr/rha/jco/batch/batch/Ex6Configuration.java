package fr.rha.jco.batch.batch;


import javax.sql.DataSource;

import fr.rha.jco.batch.input.StudentCsv;
import fr.rha.jco.batch.listener.Ex3Listener;
import fr.rha.jco.batch.output.StudentBddWithAddress;
import fr.rha.jco.batch.processor.Ex1Processor;
import fr.rha.jco.batch.processor.Ex6Processor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import  org.springframework.data.transaction.ChainedTransactionManager;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("ex4")
public class Ex6Configuration {
	private final JobBuilderFactory jobFactory;

	private final StepBuilderFactory stepFactory;

	@Bean
	public Job ex6Job(@Qualifier("ex6Step") Step step) {
		return this.jobFactory.get("ex6Job")
				.start(step)
				.build();
	}

	@Bean
	public Step ex6Step(@Qualifier("ex6Reader") ItemReader<StudentCsv> reader,
			final Ex6Processor processor,
			final Ex3Listener skipListener,
			@Qualifier("ex6Writer") ItemWriter<StudentBddWithAddress> writer,
			final ChainedTransactionManager chainedTransactionManager) {
		return this.stepFactory.get("ex6Step")
				.<StudentCsv, StudentBddWithAddress>chunk(10)
				.faultTolerant()
				.skip(ValidationException.class)
				.skip(FlatFileParseException.class)
				.skip(DuplicateKeyException.class)
				.skip(DataIntegrityViolationException.class) // TODO : ex-7
				.skipLimit(Integer.MAX_VALUE)
				.listener(skipListener)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.transactionManager(chainedTransactionManager)
				.build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<StudentCsv> ex6Reader(@Value("#{jobParameters[fileName]}") String fileName) {
		log.info("Reader : {}", fileName);
		return new FlatFileItemReaderBuilder<StudentCsv>()
				.name("ex6Reader")
				.resource(new FileSystemResource(fileName))
				.delimited()
				.delimiter(",")
				.names("id", "firstName", "lastName", "address", "birth")
				.fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
					setTargetType(StudentCsv.class);
				}})
				.linesToSkip(1)
				.build();
	}

	@Bean
	@StepScope
	public CompositeItemWriter<StudentBddWithAddress> ex6Writer(@Qualifier("ex6Student") JdbcBatchItemWriter<StudentBddWithAddress> ex6Student,
			@Qualifier("ex6Address") JdbcBatchItemWriter<StudentBddWithAddress> ex6Address) {
		return new CompositeItemWriterBuilder<StudentBddWithAddress>()
				.delegates(ex6Student, ex6Address)
				.build();
	}


	@Bean
	@StepScope
	public JdbcBatchItemWriter<StudentBddWithAddress> ex6Student(@Qualifier("oneDataSource") DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<StudentBddWithAddress>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO student (id, firstName, lastName, birth) VALUES (:id, :firstName, :lastName, :birth)")
				.dataSource(dataSource)
				.build();
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<StudentBddWithAddress> ex6Address(@Qualifier("twoDataSource") DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<StudentBddWithAddress>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO address (id, fullName, student_id) VALUES (:addressId, :address,  :id)")
				.dataSource(dataSource)
				.build();
	}

	@Bean
	public ChainedTransactionManager chainedTransactionManager(@Qualifier("oneDataSource") DataSource oneDataSource,
														@Qualifier("twoDataSource") DataSource twoDataSource) {
		return new ChainedTransactionManager(new DataSourceTransactionManager(oneDataSource), new DataSourceTransactionManager(twoDataSource));
	}
}
