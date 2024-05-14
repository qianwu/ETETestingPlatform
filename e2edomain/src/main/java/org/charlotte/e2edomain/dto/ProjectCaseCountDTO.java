package org.charlotte.e2edomain.dto;

import org.charlotte.e2edomain.domain.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCaseCountDTO extends BaseModel {
    private String projectId;
    private String projectName;
    private String gitPath;
    private Integer caseCount;
}
