package org.charlotte.e2edomain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseDTO {
    private String id;
    private String caseNo;
    private String projectId;
    private String projectName;
    private String categoryId;
    private Integer workflowId;
    private String path;
    private String comment;
    private Boolean isDeleted;
    private LocalDateTime createDateTime;
    private String input;
    private String output;

    private String creator;
}
