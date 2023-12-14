package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@Data
public class Resume {
    private String id;
    private String title;
    private String link;
    private String content;
  //  private List<String> industries;

    public Resume(String id,String title,String content, String link){
        this.id=id;
        this.title=title;
        this.content=content;
        this.link=link;
    }


    public Resume(String id, String title, String link) {
        this.id=id;
        this.title=title;
        this.link=link;
    }
}
