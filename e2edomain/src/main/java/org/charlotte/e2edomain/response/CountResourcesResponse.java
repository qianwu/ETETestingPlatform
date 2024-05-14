package org.charlotte.e2edomain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountResourcesResponse {
    private Long chainCount;
    private Long caseCount;
    private Long projectCount;
    private Long categoryCount;
    private Long dataToolCount;
}
