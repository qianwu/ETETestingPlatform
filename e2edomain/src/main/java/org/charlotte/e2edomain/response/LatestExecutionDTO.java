package org.charlotte.e2edomain.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LatestExecutionDTO {
    private String chainId;
    private String chainName;
    private LocalDateTime executionTime;
    private String executionId;
    private Integer testCaseCount;
    private String status;
    private String operator;
    private String desc;
}
