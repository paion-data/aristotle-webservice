package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.model.BaseEntity;

import lombok.Data;

@Data
public class GraphNodeExistRelationDTO extends BaseEntity {

    private Long temporaryId;

    private String existGraphNodeUuid;

    private String existGraphNodeRelation;

    private boolean isTarget;
}
