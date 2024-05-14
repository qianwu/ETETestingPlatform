package org.charlotte.e2edomain.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChainCreateDTO {
    private String chainName;
    private String chainType;
    private List<String> caseIdList;
}
