package com.jam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class JAMApplication {

	private static final String URL = "http://localhost:8080/JAM/api/v1/";

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(JAMApplication.class, args);

		String browser = ctx.getEnvironment().getProperty("browser");
		String incognito = ctx.getEnvironment().getProperty("incognito.parameter");

		String[] commands = new String[3];
		Runtime runtime = Runtime.getRuntime();
		try {
			if(browser.toLowerCase().contains("chrome")){ //if chrome is set
				commands[0] = browser;
				commands[1] = URL;
				commands[2] = incognito;
				runtime.exec(commands);
			} else if(browser.toLowerCase().contains("firefox")) { //if firefox is set
				commands[0] = browser;
				commands[1] = incognito;
				commands[2] = URL;
				runtime.exec(commands);
			} else {
				System.err.println("**************************************************************************************************");
				System.err.println("* Unable to run browser. Run it manually and enter address " + URL + ". *");
				System.err.println("**************************************************************************************************");
			}
		} catch (IOException | NullPointerException e) {
			System.err.println("**************************************************************************************************");
			System.err.println("* " + e.getMessage() + " *");
			System.err.println("**************************************************************************************************");
		}

	}
}


