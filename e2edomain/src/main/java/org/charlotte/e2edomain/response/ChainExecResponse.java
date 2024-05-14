package org.charlotte.e2edomain.response;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ChainExecResponse {
    private String executionId;
}
