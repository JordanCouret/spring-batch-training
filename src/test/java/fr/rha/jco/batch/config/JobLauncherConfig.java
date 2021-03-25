package fr.rha.jco.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class JobLauncherConfig {
	@Bean
	public JobLauncherTestUtils ex1JobTest() {
		return new JobLauncherTestUtils() {
			@Override
			@Autowired
			public void setJob(@Qualifier("ex1Job") Job job) {
				super.setJob(job);
			}
		};
	}
	@Bean
	public JobLauncherTestUtils ex4JobTest() {
		return new JobLauncherTestUtils() {
			@Override
			@Autowired
			public void setJob(@Qualifier("ex4Job") Job job) {
				super.setJob(job);
			}
		};
	}
	@Bean
	@Profile("ex4")
	public JobLauncherTestUtils ex6JobTest() {
		return new JobLauncherTestUtils() {
			@Override
			@Autowired
			public void setJob(@Qualifier("ex6Job") Job job) {
				super.setJob(job);
			}
		};
	}
}
