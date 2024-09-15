package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BindNodeDTO extends BaseEntity {

    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String fromId;

    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String toId;

    @NotBlank(message = Message.RELATION_MUST_NOT_BE_BLANK)
    private String relationName;
}