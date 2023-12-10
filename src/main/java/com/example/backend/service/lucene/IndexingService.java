package com.example.backend.service.lucene;

import com.example.backend.service.Searcher;
import com.example.backend.model.ResumeQuery;

import java.io.IOException;
import java.util.*;

public class IndexingService implements Searcher {

    private final ResumeSearcher searcher;

    // Constructor that initializes the IndexingService with a ResumeSearcher
    public IndexingService(String path, int limit, int threshold) throws IOException {
        searcher = new ResumeSearcher(path, limit, threshold);
    }

    // Implement the searchDocuments method to search for resumes
    @Override
    public Object searchDocuments(ResumeQuery resumeQuery) {
        try {
            return searcher.search(resumeQuery);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(); // Return an empty list if there's an exception
    }

    // Implement the searchDocumentsWithFacets method to search for resumes with facets
    @Override
    public Object searchDocumentsWithFacets(ResumeQuery resumeQuery) {
        try {
            return searcher.searchDocumentsWithFacets(resumeQuery);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>(); // Return an empty map if there's an exception
    }
}
