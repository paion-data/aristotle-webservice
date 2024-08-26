package com.paiondata.aristotle.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserCreateDTO {

    @NotBlank(message = "uidcid must not be blank!")
    private String uidcid;

    @NotBlank(message = "nickName must not be blank!")
    private String nickName;
}