package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.model.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphUpdateDTO extends BaseEntity {

    private String uuid;

    private String title;

    private String description;
}