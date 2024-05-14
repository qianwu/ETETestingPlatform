package org.charlotte.e2edomain.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Depends {
    private String sourceCaseId;
    private String sourceKey;
    private String targetKey;
}
