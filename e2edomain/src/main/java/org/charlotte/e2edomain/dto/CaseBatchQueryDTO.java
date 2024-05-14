package org.charlotte.e2edomain.dto;

import lombok.Data;

import java.util.List;


@Data
public class CaseBatchQueryDTO {
    private List<String> caseIds;
    private String fuzzySearchKey;
    private Integer limit;
}
