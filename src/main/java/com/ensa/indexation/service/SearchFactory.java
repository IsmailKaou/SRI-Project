package com.ensa.indexation.service;

import com.ensa.indexation.service.lucene.IndexingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class SearchFactory {

    @Value("${lucene.dir.path}")
    private String path;
    @Value("${lucene.result.limit}")
    private Integer limit;
    @Value("${lucene.result.threshold}")
    private Integer threshold;

    private Map<String, Searcher> searches;

    // Constructor that initializes the SearchFactory with necessary values and creates an IndexingService
    public SearchFactory(@Value("${lucene.dir.path}") String path, @Value("${lucene.result.limit}") int limit,
                         @Value("${lucene.result.threshold}") int threshold) {
        try {
            searches = new HashMap<>();

            // Initialize the IndexingService with the provided values
            searches.put(IndexingService.class.getSimpleName(), new IndexingService(path, limit, threshold));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // Add a Searcher implementation to the factory
    public boolean add(String key, Searcher search) {
        if (searches.containsKey(key))
            return false;
        searches.put(key, search);
        return true;
    }

    // Get a Searcher implementation from the factory by its key
    public Searcher get(String key) {
        if (!searches.containsKey(key))
            throw new RuntimeException("Key not found");
        return searches.get(key);
    }
}
