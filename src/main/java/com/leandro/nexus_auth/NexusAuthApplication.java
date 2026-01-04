package com.leandro.nexus_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableAsync // <-- Adicionado para o Sentinel
public class NexusAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(NexusAuthApplication.class, args);
	}

}
