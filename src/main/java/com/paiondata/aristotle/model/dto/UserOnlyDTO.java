package com.paiondata.aristotle.model.dto;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

@Data
public class UserOnlyDTO {

    @Property("uidcid")
    private String uidcid;

    @Property("nick_name")
    private String nickName;
}