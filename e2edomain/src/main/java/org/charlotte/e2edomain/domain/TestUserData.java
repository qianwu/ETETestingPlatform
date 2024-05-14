package org.charlotte.e2edomain.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "d_test_user_data")
public class TestUserData extends BaseModel {
    private String name;
    private String channel;
    private String mobile;
    private String partyUid;
    private String token;
    private String env;
}
