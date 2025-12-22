package com.karan.craftingtable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CraftingTableApplication {

	public static void main(String[] args) {
		SpringApplication.run(CraftingTableApplication.class, args);
	}

}
