package fr.rha.jco.batch.processor;

import javax.validation.constraints.NotNull;

import fr.rha.jco.batch.input.StudentCsv;
import fr.rha.jco.batch.output.StudentBdd;
import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class Ex1Processor implements ItemProcessor<StudentCsv, StudentBdd> {
	private final SpringValidator<StudentCsv> validator;

	@Override
	public StudentBdd process(StudentCsv studentCsv) throws Exception {
		this.validator.validate(studentCsv);
		return new StudentBdd(studentCsv);
	}
}
