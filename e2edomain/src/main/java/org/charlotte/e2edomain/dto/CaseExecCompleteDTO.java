package org.charlotte.e2edomain.dto;

import org.charlotte.e2edomain.enums.CaseExecStatus;
import lombok.Data;

import java.util.Map;


@Data
public class CaseExecCompleteDTO {
    private String chainDataId;
    private Map<String, String> reqBody;
    private Map<String, String> respBody;
    private Integer currentCaseIndex;
    private CaseExecStatus testExecutionStatus;
    private String caseExecutionLog;
}
