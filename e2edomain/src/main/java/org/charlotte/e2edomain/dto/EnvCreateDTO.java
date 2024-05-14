package org.charlotte.e2edomain.dto;

import lombok.Data;

import java.util.Map;

@Data
public class EnvCreateDTO {
    private String projectId;
    private String domain;
    private String mockServer;
    private Map<String, String> headers;
    private Map<String, String> cookies;
}
