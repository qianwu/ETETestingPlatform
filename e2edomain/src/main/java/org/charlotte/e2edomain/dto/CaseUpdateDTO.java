package org.charlotte.e2edomain.dto;


import org.charlotte.e2edomain.domain.Case;
import lombok.Data;

@Data
public class CaseUpdateDTO {

    private String id;
    private String caseNo;
    private String projectId;
    private String categoryId;
    private Integer workflowId;
    private String input;
    private String output;
    private String path;
    private String comment;

    public void copyNotNullValueTo(Case kase){
        kase.setProjectId(projectId);
        kase.setComment(comment);
        kase.setCategoryId(categoryId);
        kase.setWorkflowId(workflowId);
        kase.setOutput(output);
        kase.setInput(input);
        kase.setPath(path);
    }
}
