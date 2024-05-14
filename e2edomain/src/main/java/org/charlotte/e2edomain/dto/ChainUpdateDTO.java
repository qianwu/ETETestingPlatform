package org.charlotte.e2edomain.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChainUpdateDTO {
    private String id;
    private String chainName;
    private String chainType;
    private List<String> caseIdList;
}
