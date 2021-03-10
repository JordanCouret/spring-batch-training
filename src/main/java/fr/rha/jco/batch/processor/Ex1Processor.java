package fr.rha.jco.batch.processor;

import fr.rha.jco.batch.input.StudentCsv;
import fr.rha.jco.batch.output.StudentBdd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class Ex1Processor implements ItemProcessor<StudentCsv, StudentBdd> {
	private final SpringValidator<StudentCsv> validator;

	@Override
	public StudentBdd process(StudentCsv studentCsv) {
		this.validator.validate(studentCsv);
		log.info("{} is valid", studentCsv.toString());
		return new StudentBdd(studentCsv);
	}
}
