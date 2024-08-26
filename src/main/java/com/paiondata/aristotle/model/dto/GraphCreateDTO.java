package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.model.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GraphCreateDTO extends BaseEntity {

    @NotBlank(message = "title must not be blank!")
    private String title;

    @NotBlank(message = "description must not be blank!")
    private String description;
}