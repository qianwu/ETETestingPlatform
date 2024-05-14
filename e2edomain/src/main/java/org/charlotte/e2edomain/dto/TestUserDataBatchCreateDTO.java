package org.charlotte.e2edomain.dto;

import lombok.Data;


@Data
public class TestUserDataBatchCreateDTO {
    private String name;
    private String channel;
    private String mobile;
    private String partyUid;
    private String token;
    private String env;
}
