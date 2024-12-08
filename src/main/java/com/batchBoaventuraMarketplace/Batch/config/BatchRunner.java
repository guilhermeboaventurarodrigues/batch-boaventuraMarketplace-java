package com.batchBoaventuraMarketplace.Batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import org.springframework.stereotype.Component;

@Component
public class BatchRunner implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Override
    public void run(String... args) throws Exception {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(job, jobParameters);
            System.out.println("Batch execution status: " + execution.getStatus());
        } catch (Exception e) {
            System.err.println("Erro ao executar o job: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
