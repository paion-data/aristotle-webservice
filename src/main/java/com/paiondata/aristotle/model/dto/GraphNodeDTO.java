package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.model.BaseEntity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class GraphNodeDTO extends BaseEntity {

    @NotNull(message = Message.TEMPORARY_ID_MUST_NOT_NULL)
    private Long temporaryId;

    @NotBlank(message = Message.TITLE_MUST_NOT_BE_BLANK)
    private String title;

    @NotBlank(message = Message.DESCRIPTION_MUST_NOT_BE_BLANK)
    private String description;
}
