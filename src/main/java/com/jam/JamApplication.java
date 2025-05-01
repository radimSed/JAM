package com.jam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;

@SpringBootApplication
public class JamApplication {

	public static void main(String[] args) {
		SpringApplication.run(JamApplication.class, args);
	}

	@EventListener({ApplicationReadyEvent.class})
	void applicationReadyEvent() {
		System.out.println("Application started ... launching browser now");
		browse("http://localhost:8080/JAM/");
	}

	public static void browse(String url) {
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec("/opt/google/chrome/chrome " + url + " -incognito");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
