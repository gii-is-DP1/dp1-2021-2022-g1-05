package org.springframework.samples.parchisYOca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication()
public class ParchisYOcaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParchisYOcaApplication.class, args);
		log.info("{} has been started", ParchisYOcaApplication.class.getSimpleName());
	}

}
