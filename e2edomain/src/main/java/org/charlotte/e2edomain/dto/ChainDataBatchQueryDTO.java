package org.charlotte.e2edomain.dto;

import lombok.Data;


@Data
public class ChainDataBatchQueryDTO {
    private String chainId;
    private String status;
    private Integer limit;
}
