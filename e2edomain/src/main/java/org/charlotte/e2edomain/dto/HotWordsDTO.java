package org.charlotte.e2edomain.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class HotWordsDTO {
    private String id;
    private String keyWordAndSearchNum;

    private Boolean isDeleted;
}
