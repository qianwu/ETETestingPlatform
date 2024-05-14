package org.charlotte.e2edomain.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "d_project")
public class Project extends BaseModel {
    private String name;
    private String gitOwner;
//    private String gitRepo;

    private String gitPath;
    private String description;

    private String creator;

    private Integer workflowId;

    private Boolean isDeleted;

    // the case count of this project
    private Integer caseCount;

    // package list used for run regression from test-engine UI page
    private List<String> packageList;

    // class_name_list used for run regression from test-engine UI page
    private List<String> classNameList;

    // tags_list used for run regression from test-engine UI page
    private List<String> tagList;
}
