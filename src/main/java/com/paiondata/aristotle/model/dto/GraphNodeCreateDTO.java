package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.model.BaseEntity;

import lombok.Data;

import java.util.List;

import javax.validation.constraints.NotBlank;

@Data
public class GraphNodeCreateDTO extends BaseEntity {

    @NotBlank(message = Message.TEMPORARY_ID_MUST_NOT_BE_BLANK)
    private Long temporaryId;

    @NotBlank(message = Message.TITLE_MUST_NOT_BE_BLANK)
    private String title;

    @NotBlank(message = Message.DESCRIPTION_MUST_NOT_BE_BLANK)
    private String description;

    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String graphUuid;

    private List<GraphNodeExistDTO> graphNodeExistDTO;

    private List<GraphNodeTemporaryDTO> graphNodeTemporaryDTO;
}