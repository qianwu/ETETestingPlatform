package org.charlotte.e2edomain.dto;

import org.charlotte.e2edomain.domain.Case;
import org.charlotte.e2edomain.dto.CaseDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class ChainDTO {
    private String name;
    private List<CaseDTO> caseList;
}
