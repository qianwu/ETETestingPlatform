package org.charlotte.e2edomain.dto;

import org.charlotte.e2edomain.domain.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestUserDataDTO extends BaseModel {
    private String name;
    private String channel;
    private String mobile;
    private String partyUid;
    private String token;
    private String env;
}
