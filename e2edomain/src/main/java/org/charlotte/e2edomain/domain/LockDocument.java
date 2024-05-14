package org.charlotte.e2edomain.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;


@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "d_lock")
public class LockDocument {
    @MongoId
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    private long expireAt;
    private String token;
}
