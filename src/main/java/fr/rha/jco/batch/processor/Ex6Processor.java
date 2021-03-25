package fr.rha.jco.batch.processor;

import fr.rha.jco.batch.input.StudentCsv;
import fr.rha.jco.batch.output.StudentBdd;
import fr.rha.jco.batch.output.StudentBddWithAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
public class Ex6Processor implements ItemProcessor<StudentCsv, StudentBddWithAddress> {
	private final SpringValidator<StudentCsv> validator;

	private final JdbcTemplate jdbcTemplate;

	public Ex6Processor(SpringValidator<StudentCsv> validator, @Qualifier("twoJdbcTemplate") JdbcTemplate jdbcTemplate) {
		this.validator = validator;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public StudentBddWithAddress process(StudentCsv studentCsv) {
		this.validator.validate(studentCsv);
		if (studentCsv.getAddress() != null && studentCsv.getAddress().length() > 255) { // ex-7
			throw new ValidationException("ADDRESSE LONGUE");
		}
		log.info("{} is valid", studentCsv.toString());
		return new StudentBddWithAddress(studentCsv, jdbcTemplate.queryForObject("SELECT nextval('address_seq')", Long.class));
	}
}
