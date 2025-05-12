package com.jam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class JamApplication {

	private static final String URL = "http://localhost:8080/JAM/";

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(JamApplication.class, args);

		String browser = ctx.getEnvironment().getProperty("browser");
		String incognito = ctx.getEnvironment().getProperty("incognito.parameter");

		Runtime runtime = Runtime.getRuntime();
		try {
			if(browser.toLowerCase().contains("chrome")){ //if chrome is set
				runtime.exec(browser + " " + URL + " " + incognito);
			} else if(browser.toLowerCase().contains("firefox")) { //if firefox is set
				runtime.exec(browser + " " + incognito + " " + URL);
			} else {
				System.err.println("**************************************************************************************************");
				System.err.println("* Unable to run browser. Run it manually and enter address " + URL + ". *");
				System.err.println("**************************************************************************************************");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}


