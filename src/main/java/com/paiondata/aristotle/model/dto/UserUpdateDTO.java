package com.paiondata.aristotle.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserUpdateDTO {

    @NotBlank(message = "elementId must not be blank!")
    private String elementId;

    @NotBlank(message = "nickName must not be blank!")
    private String nickName;
}