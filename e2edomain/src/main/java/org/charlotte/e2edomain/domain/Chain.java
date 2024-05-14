package org.charlotte.e2edomain.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "d_chain")
public class Chain extends BaseModel {
    private String name;
    private String type;
    private List<Case> caseList;
    private Boolean isDeleted;
//    private Map<String, List<Depends>> depList;

    private String creator;
}
