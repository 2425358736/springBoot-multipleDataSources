package com.multiple.data.sources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * SpringBoot启动类
 * @author admin
 */
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true)
@Slf4j
public class ServiceApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("服务已启动");
	}
}
