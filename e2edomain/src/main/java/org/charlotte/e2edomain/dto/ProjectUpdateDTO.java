package org.charlotte.e2edomain.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectUpdateDTO {
    private String id;
    private String name;
    private String description;
    private String gitPath;
    private Integer workflowId;


    private Integer caseCount;

    // package list used for run regression from test-engine UI page
    private List<String> packageList;
    // class_name_list used for run regression from test-engine UI page
    private List<String> classNameList;
    // tags_list used for run regression from test-engine UI page
    private List<String> tagList;
}
