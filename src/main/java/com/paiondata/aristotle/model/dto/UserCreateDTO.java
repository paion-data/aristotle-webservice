package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.model.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserCreateDTO extends BaseEntity {

    @NotBlank(message = "uidcid must not be blank!")
    private String uidcid;

    @NotBlank(message = "nickName must not be blank!")
    private String nickName;
}