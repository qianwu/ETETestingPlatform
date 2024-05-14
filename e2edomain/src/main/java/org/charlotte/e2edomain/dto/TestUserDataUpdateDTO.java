package org.charlotte.e2edomain.dto;

import lombok.Data;

@Data
public class TestUserDataUpdateDTO {
    private String id;
    private String name;
    private String channel;
    private String mobile;
    private String partyUid;
    private String token;
    private String env;
}
