package org.charlotte.e2edomain.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "d_chain_data")
public class ChainData extends BaseModel {
    private Map<String, String> globalData;
    private Chain chainSnapshot;
    private List<CaseData> caseDataList;
    private int latestFinishedCaseIndex;
    private String currCaseId;
    private String initData;
    private String env;
    private String test;
    //chain每次执行 可能对应零个或一个jiraId
    private String jiraId;

    public boolean isAtHead() {
        return latestFinishedCaseIndex == -1;
    }

    public boolean hasNextCase() {
        return latestFinishedCaseIndex + 2 <= chainSnapshot.getCaseList().size();
    }

    public Case currentCase() {
        return this.chainSnapshot.getCaseList().get(latestFinishedCaseIndex);
    }

    public Case nextCase() {
        return this.chainSnapshot.getCaseList().get(latestFinishedCaseIndex + 1);
    }
}
