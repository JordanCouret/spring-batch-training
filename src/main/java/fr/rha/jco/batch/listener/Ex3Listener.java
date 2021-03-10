package fr.rha.jco.batch.listener;

import fr.rha.jco.batch.input.StudentCsv;
import fr.rha.jco.batch.output.StudentBdd;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

@StepScope
@Component
@Slf4j
public class Ex3Listener implements SkipListener<StudentCsv, StudentBdd> {

	@Override
	public void onSkipInRead(Throwable t) {
		if (t instanceof FlatFileParseException) {
			log.error("READ : {},{}", ((FlatFileParseException) t).getInput(), t.getClass().getSimpleName());
			return ;
		}
		log.error("READ : {}", t.getMessage());
	}

	@Override
	public void onSkipInWrite(StudentBdd dto, Throwable t) {
		log.error("WRITE : {} = {}", t.getMessage(), dto.toString());
	}

	@Override
	public void onSkipInProcess(StudentCsv dto, Throwable t) {
		log.error("PROCESS : {} = {}", t.getMessage(), dto.toString());
	}
}
