package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.model.BaseEntity;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Builder
@Data
public class UserCreateDTO extends BaseEntity {

    @NotBlank(message = Message.UIDCID_MUST_NOT_BE_BLANK)
    private String uidcid;

    @NotBlank(message = Message.NICK_NAME_MUST_NOT_BE_BLANK)
    private String nickName;
}