package com.ensa.indexation.service;

import com.ensa.indexation.model.ResumeQuery;

public interface Searcher {
    Object searchDocuments( ResumeQuery resumeQuery);
    Object searchDocumentsWithFacets(ResumeQuery resumeQuery) ;
}
