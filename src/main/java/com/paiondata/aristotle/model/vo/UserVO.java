package com.paiondata.aristotle.model.vo;

import com.paiondata.aristotle.model.BaseEntity;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class UserVO extends BaseEntity {

    private String uidcid;

    private String nickName;

    private List<Map<String, Object>> graphs;
}