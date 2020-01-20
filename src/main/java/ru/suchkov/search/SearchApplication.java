package ru.suchkov.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.suchkov.search.controller.ApplicationController;

@SpringBootApplication
public class SearchApplication implements CommandLineRunner {

	@Autowired
	private ApplicationController controller;

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(SearchApplication.class);
		application.run(args);
	}

	@Override
	public void run(String... args) {
		controller.start(args);
	}
}
