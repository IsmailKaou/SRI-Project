package com.ensa.indexation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Resume {
    private int id;
    private String title;
    private String link;
    private String content;
    private List<String> industries;
}
