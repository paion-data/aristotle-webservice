package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.model.BaseEntity;
import lombok.Data;

@Data
public class GraphUpdateDTO extends BaseEntity {

    private String uuid;

    private String title;

    private String description;
}