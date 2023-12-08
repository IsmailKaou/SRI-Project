package com.ensa.indexation.service.lucene;

import com.ensa.indexation.model.Resume;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class ResumeIndexer {
    public final static String TITLE = "title";
    public final static String ID = "id";
    public final static String LINK = "link";
    public final static String CONTENT = "content";
    public final static String INDUSTRIES = "industries";
    public static FacetsConfig facetsConfig = new FacetsConfig();
    private final Path path;
    static {
        facetsConfig.setMultiValued(ResumeIndexer.INDUSTRIES, true);
        facetsConfig.setRequireDimCount(ResumeIndexer.INDUSTRIES, true);
    }

    public ResumeIndexer(String directoryPath) {
        path = Paths.get(directoryPath);
    }

    // Index a list of resumes into the Lucene index
    public void index(List<Resume> resumes) throws IOException {
        Directory dir = FSDirectory.open(path);

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        config.setRAMBufferSizeMB(256);
        IndexWriter writer = new IndexWriter(dir, config);
        addDocument(resumes.iterator(), writer);
        writer.commit();
        writer.close();
    }

    // Get the number of indexed documents in the Lucene index
    public static int getNumberOfIndexedDocuments(String directoryPath) {
        int numDocs = 0;
        try {
            Directory directory = FSDirectory.open(Paths.get(directoryPath));
            try (DirectoryReader directoryReader = DirectoryReader.open(directory)) {
                numDocs = directoryReader.numDocs();
            } catch (IOException e) {
                // Handle exception
                e.printStackTrace();
            }
        } catch (IOException e) {
            // Handle exception
            e.printStackTrace();
        }
        return numDocs;
    }

    // Add documents to the Lucene index
    private void addDocument(Iterator<Resume> resumes, IndexWriter writer) throws IOException {
        while (resumes.hasNext()) {
            writer.addDocument(createDocument(resumes.next()));
        }
    }

    // Create a Lucene Document from a Resume object
    private Document createDocument(Resume resume) throws IOException {
        System.out.println(resume.getId());

        IndexableField id = new StoredField(ID, String.valueOf(resume.getId()));
        IndexableField title = new TextField(TITLE, resume.getTitle(), Field.Store.YES);
        IndexableField link = new StoredField(LINK, resume.getLink());
        IndexableField content = new TextField(CONTENT, resume.getContent(), Field.Store.YES);

        Document doc = new Document();
        doc.add(id);
        doc.add(title);
        doc.add(link);
        doc.add(content);

        // Add industries and facets to the document
        for (String industry : resume.getIndustries()) {
            if (industry != null && !industry.isEmpty()) {
                IndexableField industriesFacets = new SortedSetDocValuesFacetField(INDUSTRIES, industry);
                IndexableField industries = new TextField(INDUSTRIES, industry, Field.Store.YES);
                doc.add(industriesFacets);
                doc.add(industries);
            }
        }

        // Build the document using the facets configuration
        return facetsConfig.build(doc);
    }
}
