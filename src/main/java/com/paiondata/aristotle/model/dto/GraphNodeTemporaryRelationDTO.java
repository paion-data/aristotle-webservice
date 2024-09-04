package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.model.BaseEntity;

import lombok.Data;

@Data
public class GraphNodeTemporaryRelationDTO extends BaseEntity {

    private Long temporaryId1;

    private Long temporaryId2;

    private String temporaryRelation;

    private boolean isTarget;
}
