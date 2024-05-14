package org.charlotte.e2edomain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseCountInfoDTO {
    private Integer count;
//    private String gitPath;
    private Date createDateTime;
}
