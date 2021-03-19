package fr.rha.jco.batch.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class Ex1Configuration {

	private final JobBuilderFactory jobFactory;

	private final StepBuilderFactory stepFactory;

	@Bean
	public Job ex1Job(@Qualifier("ex1Step") Step step) {
		return this.jobFactory.get("ex1Job")
				.start(step)
				.build();
	}

	@Bean
	public Step ex1Step() {
		return null;
	}
}
