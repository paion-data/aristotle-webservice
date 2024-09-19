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
 * Data Transfer Object (DTO) for deleting graphs.
 *
 * This DTO is used to encapsulate the data required for deleting graphs.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data Transfer Object (DTO) for deleting graphs.")
public class GraphDeleteDTO {

    /**
     * The uidcid of user.
     */
    @ApiModelProperty(value = "The uidcid of user.", required = true)
    @NotBlank(message = Message.UIDCID_MUST_NOT_BE_BLANK)
    String uidcid;

    /**
     * The uuids of graphs.
     */
    @ApiModelProperty(value = "The uuids of graphs.", required = true)
    @NotEmpty(message = Message.UUID_MUST_NOT_BE_BLANK)
    List<String> uuids;
}
