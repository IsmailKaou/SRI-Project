package com.example.backend;

import com.example.backend.model.Resume;
import com.example.backend.service.lucene.ResumeIndexer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IndexationApplication {



    public static void main(String[] args) {




        SpringApplication.run(IndexationApplication.class, args);
    }

}
