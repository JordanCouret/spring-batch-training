package fr.rha.jco.batch.batch;

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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DuplicateKeyException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class Ex1Configuration {

	private final JobBuilderFactory jobFactory;

	private final StepBuilderFactory stepFactory;

	@Bean
	public Job ex1Job(@Qualifier("ex1Step") Step step)  {
		return this.jobFactory.get("ex1Job")
				.start(step)
				.build();
	}

	@Bean
	public Step ex1Step(@Qualifier("ex1Reader") ItemReader<StudentCsv> reader,
						final Ex1Processor processor,
						final Ex3Listener skipListener) {
		return this.stepFactory.get("ex1Step")
				.<StudentCsv, StudentBdd>chunk(10)
				.faultTolerant()
				.skip(ValidationException.class)
				.skip(FlatFileParseException.class)
				.skip(DuplicateKeyException.class)
				.skipLimit(Integer.MAX_VALUE)
				.listener(skipListener)
				.reader(reader)
				.processor(processor)
				// Set by spring-batch, so null
				.writer(this.ex1Writer(null))
				.build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<StudentCsv> ex1Reader(@Value("#{jobParameters[fileName]}") String fileName) {
		log.info("Reader : {}", fileName);
		return new FlatFileItemReaderBuilder<StudentCsv>()
				.name("ex1Reader")
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
	public JdbcBatchItemWriter<StudentBdd> ex1Writer(final DataSource dataSource) {
		log.info("Writer : {}", dataSource);
		return new JdbcBatchItemWriterBuilder<StudentBdd>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO student (id, firstName, lastName, address, birth) VALUES (:id, :firstName, :lastName, :address, :birth)")
				.dataSource(dataSource)
				.build();
	}


}
