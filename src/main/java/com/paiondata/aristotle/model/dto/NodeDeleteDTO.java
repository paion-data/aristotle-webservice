package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.common.base.Message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Data Transfer Object (DTO) for deleting nodes.
 *
 * This DTO is used to encapsulate the data required for deleting nodes.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data Transfer Object (DTO) for deleting nodes.")
public class NodeDeleteDTO {

    /**
     * The uidcid of graph.
     */
    @ApiModelProperty(value = "The uuid of graph.", required = true)
    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    String uuid;

    /**
     * The uuids of nodes.
     */
    @ApiModelProperty(value = "The uuids of nodes.", required = true)
    @NotEmpty(message = Message.UUID_MUST_NOT_BE_BLANK)
    List<String> uuids;
}
