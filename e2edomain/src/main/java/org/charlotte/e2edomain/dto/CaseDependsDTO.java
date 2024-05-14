package org.charlotte.e2edomain.dto;

import lombok.Data;

@Data
public class CaseDependsDTO {
    private String dependsCaseId;
    private String sourceCaseId;
    private String sourceKey;
    private String targetKey;
}
