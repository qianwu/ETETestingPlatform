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
@Document(collection = "d_env")
public class Env extends BaseModel {
    private String projectId;
    private String domain;
    private String mockServer;
    private Map<String, String> headers;
    private Map<String, String> cookies;
}
