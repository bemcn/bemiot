package org.bem.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author jakybland
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class BemiotApplication {

	public static void main(String[] args) {
		SpringApplication.run(BemiotApplication.class, args);
	}

}
