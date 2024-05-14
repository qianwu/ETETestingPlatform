package org.charlotte.e2edomain.dto;


import lombok.Data;

@Data
public class CaseCreateDTO {
    private String caseNo;
    private String projectId;
    private String categoryId;

    private Integer workflowId;

    private String input;
    private String output;

    private String path;
    //    private String queryStr;
//    private String reqBodyType;
//    private String reqBody;
//    private String respBodyType;
//    private String respBody;
//    private Map<String, String> headers;
//    private Map<String, String> cookies;
    private String comment;

//    private String creator;
}
