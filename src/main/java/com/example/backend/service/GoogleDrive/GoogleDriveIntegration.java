package com.example.backend.service.GoogleDrive;

import com.example.backend.model.Resume;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleDriveIntegration {

    @Getter
    private static List<Resume> CVs=new ArrayList<>();

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Systéme de recherche de documents";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    @Value("${folderId}")
    static String folderId = "1zZKFo3rLREQWYI-P52wxFrNHVPcnpaoD";

    static Drive service;
    static NetHttpTransport HTTP_TRANSPORT;

    private static List<com.google.api.services.drive.model.File> files;
    @PostConstruct
    public void init() throws GeneralSecurityException, IOException {
        // Your custom initialization logic here
       GoogleDriveIntegration.googleDriveInt();
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = GoogleDriveIntegration.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return credential;
    }



    private static String getPdfContent(Drive service, String fileId) throws IOException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            service.files().get(fileId).executeMediaAndDownloadTo(outputStream);

            PDDocument document = PDDocument.load(outputStream.toByteArray());
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);


            document.close();
            return text;
        } catch (IOException e) {
            System.err.println("Error getting file content: " + e.getMessage());
            throw e;
        }
    }

    private static String getDocxContent(Drive service, String fileId) {
        System.out.println("im in docx extractor");
        XWPFWordExtractor extractor = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            service.files().get(fileId).executeMediaAndDownloadTo(outputStream);

            XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(outputStream.toByteArray()));
            extractor = new XWPFWordExtractor(document);
            String fileData = extractor.getText();
            //System.out.println(fileData);
            return  fileData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void googleDriveInt() throws IOException, GeneralSecurityException {
       // 1bob4VxTmU3WLDZoGCAG_27f-xB_hBXa5

        String fileContent="";
        String query = "'" + folderId + "' in parents";

        // Build a new authorized API client service.
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
                .setQ(query)
//                .setPageSize(10)
                .setFields("nextPageToken, files(id, name, fileExtension)")
                .execute();
        files = result.getFiles();

        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (com.google.api.services.drive.model.File file : files) {


                // Ajouter le nom de fichier dans la liste des cvs
                CVs.add(new Resume(file.getId(),file.getName(),"https://drive.google.com/file/d/"+file.getId()+"/view",file.getFileExtension()));

                System.out.printf("file name:%s (%s) extension%s %s \n", file.getName(), file.getId(),file.getFileExtension(),"https://drive.google.com/file/d/"+file.getId()+"/view");

            }
        }
    }

    public static List<Resume> extractCVsContent() throws IOException {
        String fileContent="";
        CVs=new ArrayList<>();
        for (com.google.api.services.drive.model.File file : files) {


            System.out.printf("%s (%s) %s %s \n", file.getName(), file.getId(),file.getFileExtension(),"https://drive.google.com/file/d/"+file.getId()+"/view");

                if(file.getFileExtension().equals("pdf")){
                    fileContent = getPdfContent(service,file.getId());
                    System.out.println("PDF File Content:");
                    System.out.println(fileContent);
                } else if (file.getFileExtension().equals("docx") || file.getFileExtension().equals("doc")){
                    fileContent = getDocxContent(service,file.getId());
                    System.out.println("DOCX File Content:");
                    System.out.println(fileContent);
                } else {
                    System.out.println("File type not supported");
                }

            // Ajouter les fichier dans la liste des cvs
            CVs.add(new Resume(file.getId(),file.getName(),fileContent,file.getFileExtension(),"https://drive.google.com/file/d/"+file.getId()+"/view"));
        }
        return CVs;
    }


}
