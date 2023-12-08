package com.ensa.indexation;

import com.ensa.indexation.model.Resume;
import com.ensa.indexation.service.lucene.ResumeIndexer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class IndexationApplication {



    public static void main(String[] args) {




        SpringApplication.run(IndexationApplication.class, args);
    }

}
