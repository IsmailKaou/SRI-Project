package com.example.backend;

import com.example.backend.model.Resume;
import com.example.backend.service.GoogleDrive.GoogleDriveIntegration;
import com.example.backend.service.lucene.ResumeIndexer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SpringBootApplication
public class IndexationApplication {



    public static void main(String[] args) throws GeneralSecurityException, IOException {



   //     GoogleDriveIntegration.googleDriveInt();
        SpringApplication.run(IndexationApplication.class, args);
    }

}
