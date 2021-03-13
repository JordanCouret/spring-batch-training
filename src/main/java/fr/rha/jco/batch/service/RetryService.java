package fr.rha.jco.batch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RetryService {

	private final JobExplorer jobExplorer;

	private final JobOperator jobOperator;

	public JobExecution retry(JobExecution execution) throws JobParametersInvalidException, JobRestartException, JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, NoSuchJobException {
		log.info("Tentative de rejeu pour l'identifiant du job {}", execution.getId());
		final Long restart = jobOperator.restart(execution.getId());
		return this.jobExplorer.getJobExecution(restart);
	}
}
