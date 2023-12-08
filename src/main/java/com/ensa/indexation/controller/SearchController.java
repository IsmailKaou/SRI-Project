package com.ensa.indexation.controller;

import com.ensa.indexation.model.ResumeQuery;
import com.ensa.indexation.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@CrossOrigin(originPatterns = "*")
@RequestMapping("/api/v1/search")
public class SearchController {
    private SearchService searchService;

    // Constructor injection of SearchService
    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    // Endpoint to search for documents using Lucene
    @PostMapping("/lucene")
    public Object findInLucene(@RequestBody ResumeQuery resumeQuery) {
        System.out.println(resumeQuery);

        // Check if the incoming resumeQuery is null
        if (resumeQuery == null) {
            // If it's null, return an empty Object
            return new Object();
        }

        // Check if the "find" attribute of resumeQuery is null
        if (resumeQuery.getFind() == null) {
            // If it's null, set it to an empty string
            resumeQuery.setFind("");
        }

        // Delegate the search operation to the searchService and return the result
        return searchService.searchDocumentsOnLucene(resumeQuery);
    }

    // A dummy endpoint for testing purposes
    @GetMapping("/world")
    public Object dummy() {
        // Return a list containing "Hello" and "World"
        return Arrays.asList("Hello", "World");
    }
}
