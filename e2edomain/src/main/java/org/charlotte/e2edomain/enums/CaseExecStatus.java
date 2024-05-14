package org.charlotte.e2edomain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CaseExecStatus {
    PASSED("executed successfully"),
    FAILED("error occurs while executed"),
    TIMEOUT("call back time out");

    private String desc;
}
