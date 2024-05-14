package org.charlotte.e2edomain.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "d_case")
public class Case extends BaseModel {
    private String caseNo;
    private String projectId;
    private String categoryId;

    @Field(targetType = FieldType.INT32)
    private Integer workflowId;

    private String input;
    private String output;

    private String path;
//    private String queryStr;
//    private String reqBodyType;
//    private String reqBody;
//    private String respBodyType;
//    private String respBody;
//    private Map<String, String> headers;
//    private Map<String, String> cookies;
    private String comment;
    private Boolean isDeleted;

    private String creator;
}
