package org.charlotte.e2edomain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChainBatchQueryDTO {
    private Integer limit;
    private String type;
}
