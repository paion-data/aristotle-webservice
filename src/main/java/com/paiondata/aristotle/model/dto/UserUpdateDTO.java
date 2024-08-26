package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.model.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserUpdateDTO extends BaseEntity {

    @NotBlank(message = "elementId must not be blank!")
    private String elementId;

    @NotBlank(message = "nickName must not be blank!")
    private String nickName;
}