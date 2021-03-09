package fr.rha.jco.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
	@Bean
	public Validator localValidatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public <T> SpringValidator<T> springValidatorItem() {
		SpringValidator<T> springValidator = new SpringValidator<>();
		springValidator.setValidator(this.localValidatorFactoryBean());
		return springValidator;
	}
}
