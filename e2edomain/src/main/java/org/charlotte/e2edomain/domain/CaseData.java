package org.charlotte.e2edomain.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "d_case_data")
public class CaseData extends BaseModel {
    private String caseNo;
    private String projectId;

    private String queryStr;
    private String reqBody;
    private String respBody;
    private String log;
    private Map<String, String> headers;
}
