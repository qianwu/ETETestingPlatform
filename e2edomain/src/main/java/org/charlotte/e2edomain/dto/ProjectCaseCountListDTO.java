package org.charlotte.e2edomain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCaseCountListDTO {
    private String projectId;
    private String projectName;
    private String gitPath;

    private List<CaseCountInfoDTO> caseCountList;
}
