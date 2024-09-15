package com.paiondata.aristotle.model.vo;

import com.paiondata.aristotle.model.BaseEntity;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Builder
@Data
public class GraphVO extends BaseEntity {

    private String uuid;

    private String title;

    private String description;

    private String createTime;

    private String updateTime;

    private List<Map<String, Map<String, Object>>> nodes;
}
