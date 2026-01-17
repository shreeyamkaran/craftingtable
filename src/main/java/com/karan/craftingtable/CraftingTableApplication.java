package com.karan.craftingtable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories
@EnableMethodSecurity
public class CraftingTableApplication {

	public static void main(String[] args) {
		SpringApplication.run(CraftingTableApplication.class, args);
	}
}
