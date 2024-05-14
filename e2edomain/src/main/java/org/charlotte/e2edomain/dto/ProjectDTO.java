package org.charlotte.e2edomain.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ProjectDTO {
    private String id;
    private String name;
    private String gitPath;
    private String gitOwner;
    private String description;
    private String creator;
    private Integer workflowId;

    private Boolean isDeleted;
}
