package com.paiondata.aristotle.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Represents a graph")
public class GraphVO {

    /**
     * The UUID of the graph.
     */
    @ApiModelProperty(value = "The UUID of the graph")
    private String uuid;

    /**
     * The title of the graph.
     */
    @ApiModelProperty(value = "The title of the graph")
    private String title;

    /**
     * The description of the graph.
     */
    @ApiModelProperty(value = "The description of the graph")
    private String description;

    /**
     * The creation time of the graph.
     */
    @ApiModelProperty(value = "The creation time of the graph")
    private String createTime;

    /**
     * The last update time of the graph.
     */
    @ApiModelProperty(value = "The last update time of the graph")
    private String updateTime;

    /**
     * The node of the graph.
     */
    @ApiModelProperty(value = "The node of the graph")
    private NodeVO node;

    /**
     * The relation between nodes.
     */
    @ApiModelProperty(value = "The relation between nodes")
    private RelationVO relation;
}
