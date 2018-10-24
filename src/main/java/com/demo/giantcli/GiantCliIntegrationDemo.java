package com.demo.giantcli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Georgy Zheyts <gzheyts@gmail.com>
 */
@EnableAsync
@SpringBootApplication
public class GiantCliIntegrationDemo {
	public static void main(String[] args) {
		SpringApplication.run(GiantCliIntegrationDemo.class, args);
	}

	@Bean
	@Lazy
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		int cors = Runtime.getRuntime().availableProcessors();
		executor.setCorePoolSize(cors);
		executor.setMaxPoolSize(cors);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("Giant-cli-");
		executor.initialize();
		return executor;
	}
}
