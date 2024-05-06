package com.fir.flow.entity.common;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author dpe
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "前端组件列表")
public class DropDown {

    @ApiModelProperty(value = "名称")
    private String label;

    @ApiModelProperty(value = "id节点")
    private String value;
}
