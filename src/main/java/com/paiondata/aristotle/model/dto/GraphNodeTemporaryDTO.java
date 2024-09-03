package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.model.BaseEntity;

import lombok.Data;

@Data
public class GraphNodeTemporaryDTO extends BaseEntity {

    private String temporaryId;

    private String temporaryRelation;

    private boolean isTarget;
}
