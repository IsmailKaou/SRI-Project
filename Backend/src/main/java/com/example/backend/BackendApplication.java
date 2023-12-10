package com.example.backend;

import com.example.backend.service.GoogleDriveIntegration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) throws GeneralSecurityException, IOException {
		SpringApplication.run(BackendApplication.class, args);

//		System.out.println("Hello world!");
		GoogleDriveIntegration.googleDriveInt();
	}

}
