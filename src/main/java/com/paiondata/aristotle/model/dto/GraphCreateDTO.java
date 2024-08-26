package com.paiondata.aristotle.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GraphCreateDTO {

    @NotBlank(message = "title must not be blank!")
    private String title;

    @NotBlank(message = "description must not be blank!")
    private String description;
}