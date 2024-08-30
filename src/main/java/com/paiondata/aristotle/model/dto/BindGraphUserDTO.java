package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.model.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BindGraphUserDTO extends BaseEntity {

    @NotBlank(message = Message.UIDCID_MUST_NOT_BE_BLANK)
    private String userUidcid;

    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String graphUuid;
}