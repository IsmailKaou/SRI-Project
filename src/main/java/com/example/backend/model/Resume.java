package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@AllArgsConstructor
public class Resume {
    private String id;
    private String title;
    private String link;
    private String content;
    private String extension;
  //  private List<String> industries;



    public Resume(String id, String title, String link,String extension) {
        this.id=id;
        this.title=title;
        this.link=link;
        this.extension = extension;
    }
}
