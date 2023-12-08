package com.ensa.indexation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeQuery {

    private String find;
    private List<String> industries;

}
