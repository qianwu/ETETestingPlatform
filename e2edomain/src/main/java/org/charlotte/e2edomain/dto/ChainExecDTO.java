package org.charlotte.e2edomain.dto;

import lombok.Data;

import java.util.Map;


@Data
public class ChainExecDTO {
    private String chainId;
    private String env;
    private Map<String, String> initParams;
    private String caseId;
    private String projectId;
    //chain每次执行 可能对应零个或一个jiraId
    private String jiraId;
    private String categoryId;
}
