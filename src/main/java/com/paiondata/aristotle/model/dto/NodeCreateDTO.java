package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.model.BaseEntity;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class NodeCreateDTO extends BaseEntity {

    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String graphUuid;

    @Valid
    private List<NodeDTO> graphNodeDTO;

    private List<NodeExistRelationDTO> graphNodeExistRelationDTO;

    private List<NodeTemporaryRelationDTO> graphNodeTemporaryRelationDTO;
}