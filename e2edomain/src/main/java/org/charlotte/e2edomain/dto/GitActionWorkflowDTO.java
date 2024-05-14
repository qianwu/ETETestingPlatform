package org.charlotte.e2edomain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GitActionWorkflowDTO {
    private Long workflowId;
    private String workflowName;
}
