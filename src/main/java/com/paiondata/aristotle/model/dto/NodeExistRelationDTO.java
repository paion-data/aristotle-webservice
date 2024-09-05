package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.model.BaseEntity;
import lombok.Data;

@Data
public class NodeExistRelationDTO extends BaseEntity {

    private String fromId;

    private String toId;

    private String relationName;

    private boolean isFromIdExist;
}
