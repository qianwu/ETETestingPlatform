package org.charlotte.e2edomain.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author : charlotte
 * @date ：Created in 7/21/21 5:16 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "d_hotwords")
public class HotWords extends BaseModel {
    private Boolean isDeleted;

    //    private List<String> keyWordAndNumList;
    private String keyWordAndSearchNum;
}
