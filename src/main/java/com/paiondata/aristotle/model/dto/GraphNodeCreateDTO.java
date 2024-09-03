package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.model.BaseEntity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GraphNodeCreateDTO extends BaseEntity {

    @NotBlank(message = Message.TITLE_MUST_NOT_BE_BLANK)
    private String title;

    @NotBlank(message = Message.DESCRIPTION_MUST_NOT_BE_BLANK)
    private String description;

    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String graphUuid;

    private String graphNodeUuid;

    private String relation;

    private boolean isTarget;
}