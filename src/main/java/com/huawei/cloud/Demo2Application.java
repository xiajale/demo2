package com.huawei.cloud;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Demo2Application {

	public static void main(String[] args) {

		try {
			InitializationService.initialize();
		} catch (InitializationException e) {
			e.printStackTrace();
		}

		SpringApplication.run(Demo2Application.class, args);
	}
}
