package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class Resume {
    private String id;
    private String title;
    private String content;
    private String extension;
    private String link;
  //  private List<String> industries;



    public Resume(String id, String title,String content,String extension, String link ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.extension = extension;
        this.link = link;
    }
    public Resume(String id, String title, String link, String extension) {
        this.id = id;
        this.title = title;
        this.extension = extension;
        this.link = link;
    }
}
