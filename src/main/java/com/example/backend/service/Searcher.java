package com.example.backend.service;

import com.example.backend.model.ResumeQuery;

public interface Searcher {
    Object searchDocuments( ResumeQuery resumeQuery);
    Object searchDocumentsWithFacets(ResumeQuery resumeQuery) ;
}
