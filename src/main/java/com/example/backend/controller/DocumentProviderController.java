package com.example.backend.controller;

import com.example.backend.service.GoogleDriveIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(originPatterns = "*")
@RequestMapping("/api/v1/files")
public class DocumentProviderController {
    @Autowired
    GoogleDriveIntegration googleDriveIntegration;

    @GetMapping
    public ResponseEntity<List<String>> getCVs(){

        return ResponseEntity.ok(googleDriveIntegration.getCVs());
    }

}
