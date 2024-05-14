package org.charlotte.e2edomain.dto;

import lombok.Data;

@Data
public class ProjectCreateDTO {
    private String name;
    private String gitOwner;
    private String gitRepo;
    private String description;

    private Integer workflowId;

    private String gitPath;
}
