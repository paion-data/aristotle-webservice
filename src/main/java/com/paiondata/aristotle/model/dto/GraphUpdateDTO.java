package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.model.BaseEntity;
import lombok.Data;

@Data
public class GraphUpdateDTO extends BaseEntity {

    private String elementId;

    private String title;

    private String description;
}