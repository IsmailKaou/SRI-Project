package com.example.backend.service;

import com.example.backend.model.Resume;
import com.example.backend.model.ResumeQuery;
import com.example.backend.service.GoogleDrive.GoogleDriveIntegration;
import com.example.backend.service.lucene.ResumeIndexer;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.example.backend.service.lucene.ResumeIndexer.getNumberOfIndexedDocuments;

@Service
@Data
public class SearchService {



    @Value("${lucene.dir.path}")
    public static  String PATH="/Users/fatima-zahra/Desktop/SRI" ;
    private static final List<String> industries = Arrays.asList("Technology", "Finance", "Healthcare", "Education", "Engineering");

    public static final String LUCENE_SERVICE = "IndexingService";
    private final SearchFactory searchFactory;

    private static boolean HasIndexationBeenDone=false;
    // Helper method to get a random sublist from a list
//    private static List<String> getRandomSublist(List<String> list, int size, Random random) {
//        List<String> shuffled = new ArrayList<>(list);
//        java.util.Collections.shuffle(shuffled, random);
//        return shuffled.subList(0, size);
//    }

   //  Create dummy resumes with random industries
//    public static List<Resume> createDummyResumes(int numResumes) {
//        List<Resume> resumes = new ArrayList<>();
//        Random random = new Random();
//
//        for (int i = 1; i <= numResumes; i++) {
//            List<String> randomIndustries = getRandomSublist(industries, random.nextInt(industries.size()) + 1, random);
//            resumes.add(new Resume("i", "Resume Title " + i, "https://example.com/resume" + i + ".pdf",
//                    "This is the content of Resume " + i + ".", randomIndustries));
//        }
//
//        return resumes;
//    }

    // Clear the Lucene index
    public void clearIndex() throws IOException {
        try (Directory dir = FSDirectory.open(Paths.get(PATH));
             IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()))) {
            writer.deleteAll();
            writer.commit();
        }
    }

    // Initialize the service by creating dummy data


    public void initForSearch() throws GeneralSecurityException, IOException {

        try {

            GoogleDriveIntegration.extractCVsContent();
            clearIndex();
       //     List<Resume> dummyResumes = createDummyResumes(10);
            List<Resume> resumes= GoogleDriveIntegration.getCVs();
        // System.out.println("the resumes are "+GoogleDriveIntegration.getCVs());
            ResumeIndexer resumeIndexer = new ResumeIndexer(PATH);
            resumeIndexer.index(resumes);
        }
        catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        }
    }



    // Search for documents using Lucene
    public Object searchDocumentsOnLucene(ResumeQuery resumeQuery) throws GeneralSecurityException, IOException {
        // cette condition teste si l'indexation a ete deja faite pour ne pas le refaire et ralentir le systeme
        if (!HasIndexationBeenDone) {
            initForSearch();
            HasIndexationBeenDone=true;
        }

        try (DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(PATH)))) {
            for (int i = 0; i < reader.maxDoc(); i++) {
                Document doc = reader.document(i);
                System.out.println(doc.getField(ResumeIndexer.CONTENT).stringValue());
            //    System.out.println(doc.getField(ResumeIndexer.INDUSTRIES).stringValue());
                // Print other fields as needed
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Searcher searcher = searchFactory.get(LUCENE_SERVICE);
        return searcher.searchDocuments(resumeQuery);
    }

    // Search for documents using Lucene with facets
    // we'll see if we'll use this , for now you can ignore it
    public Object searchDocumentsOnLuceneWithFacets(ResumeQuery resumeQuery) {
        Searcher searcher = searchFactory.get(LUCENE_SERVICE);
        return searcher.searchDocumentsWithFacets(resumeQuery);
    }
}
