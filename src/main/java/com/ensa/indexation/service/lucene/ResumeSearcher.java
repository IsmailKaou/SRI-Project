package com.ensa.indexation.service.lucene;

import com.ensa.indexation.model.Resume;
import com.ensa.indexation.model.ResumeQuery;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.sortedset.DefaultSortedSetDocValuesReaderState;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetCounts;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesReaderState;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.*;

public class ResumeSearcher {
    public static final String RESULT = "result";
    public static final String FACETS = "facets";
    private Directory dir;
    private IndexSearcher searcher;
    private int limit;
    private int threshold;

    public ResumeSearcher(String path, int limit, int threshold) throws IOException {
        dir = FSDirectory.open(Paths.get(path));
        DirectoryReader indexReader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(indexReader);
        this.limit = limit;
        this.threshold = threshold;
    }

    // Tokenize the query string using the provided analyzer
    private List<String> tokenizeQuery(Analyzer analyzer, String query) {
        List<String> result = new ArrayList<>();
        try {
            TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
            stream.reset();
            while (stream.incrementToken()) {
                result.add(stream.getAttribute(CharTermAttribute.class).toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(result);
        return result;
    }

    // Search for resumes based on the given resumeQuery
    public Object search(ResumeQuery resumeQuery) throws IOException {
        Query query = getQuery(resumeQuery);
        System.out.println("Lucene Query: " + query.toString());
        TopDocs result = searcher.search(query, limit);
        if (result.totalHits.value == 0) {
            System.out.println("No documents found");
            return new ArrayList<>();
        }

        return getResumes(result);
    }

    // Retrieve resumes based on the search result
    private List<Resume> getResumes(TopDocs result) throws IOException {
        List<Resume> collection = new ArrayList<>();
        StoredFields fields = searcher.storedFields();
        for (ScoreDoc doc : result.scoreDocs) {
            Document d = fields.document(doc.doc);
            collection.add(getResume(d));
        }
        return collection;
    }

    // Search for documents with facets
    public Object searchDocumentsWithFacets(ResumeQuery resumeQuery) throws IOException {
        BooleanQuery.Builder buildQuery = buildQuery(resumeQuery);

        if (resumeQuery.getIndustries() != null && !resumeQuery.getIndustries().isEmpty()) {
            DrillDownQuery drillDownQuery = new DrillDownQuery(ResumeIndexer.facetsConfig);
            for (String g : resumeQuery.getIndustries()) {
                drillDownQuery.add(ResumeIndexer.INDUSTRIES, g);
            }
            buildQuery.add(drillDownQuery, BooleanClause.Occur.FILTER);
        }

        Query query = buildQuery.build();

        FacetsCollector facetsCollector = new FacetsCollector(true);
        TopScoreDocCollector topScoreDocCollector = TopScoreDocCollector.create(limit, threshold);
        searcher.search(query, MultiCollector.wrap(topScoreDocCollector, facetsCollector));

        TopDocs result = topScoreDocCollector.topDocs();

        if (result.totalHits.value == 0) {
            return getSearchResult();
        }

        List<Resume> collection = getResumes(result);

        SortedSetDocValuesReaderState state =
                new DefaultSortedSetDocValuesReaderState(searcher.getIndexReader(),
                        ResumeIndexer.facetsConfig);

        Facets facets = new SortedSetDocValuesFacetCounts(state, facetsCollector);

        FacetResult facetResult = facets.getAllChildren(ResumeIndexer.INDUSTRIES);

        Map<String, Object> map = getSearchResult();
        map.put(RESULT, collection);
        map.put(FACETS, facetResult);
        return map;
    }

    /**
     * Returns an empty search result.
     *
     * @return
     */
    private Map<String, Object> getSearchResult() {
        Map<String, Object> map = new HashMap<>();
        map.put(RESULT, new ArrayList<>());
        map.put(FACETS, new FacetResult("", new String[]{}, 0, new LabelAndValue[]{}, 0));
        return map;
    }

    // Create a Lucene query based on the resumeQuery
    private Query getQuery(ResumeQuery resumeQuery) {
        Query query = new MatchAllDocsQuery();
        if (resumeQuery != null) {
            System.out.println("Building query");
            return buildQuery(resumeQuery).build();
        }
        return query;
    }

    // Build a BooleanQuery for the resumeQuery
    private BooleanQuery.Builder buildQuery(ResumeQuery resumeQuery) {
        List<String> queries = tokenizeQuery(new StandardAnalyzer(), resumeQuery.getFind());
        System.out.println("First query: " + queries.get(0));
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        if (queries.isEmpty()) {
            System.out.println("No queries here");
            booleanQuery.add(new MatchAllDocsQuery(), BooleanClause.Occur.SHOULD);
        }

        for (String s : queries) {
            booleanQuery
                    .add(new TermQuery(new Term(ResumeIndexer.CONTENT, s)), BooleanClause.Occur.MUST);
        }
        return booleanQuery;
    }

    // Extract resume information from a Lucene document
    private Resume getResume(Document document) {
        Resume resume = new Resume();
        resume.setId(Integer.parseInt(document.get(ResumeIndexer.ID)));
        resume.setTitle(document.get(ResumeIndexer.TITLE));
        resume.setLink(document.get(ResumeIndexer.LINK));
        resume.setIndustries(Arrays.asList(document.getValues(ResumeIndexer.INDUSTRIES)));
        resume.setContent(document.get(ResumeIndexer.CONTENT));
        return resume;
    }
}
