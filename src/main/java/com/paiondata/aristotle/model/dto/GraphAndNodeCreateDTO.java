package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.model.BaseEntity;
import lombok.Data;
import java.util.List;
import javax.validation.Valid;

@Data
public class GraphAndNodeCreateDTO extends BaseEntity {

    @Valid
    private GraphCreateDTO graphCreateDTO;

    @Valid
    private List<NodeDTO> graphNodeDTO;

    private List<NodeTemporaryRelationDTO> graphNodeTemporaryRelationDTO;
}