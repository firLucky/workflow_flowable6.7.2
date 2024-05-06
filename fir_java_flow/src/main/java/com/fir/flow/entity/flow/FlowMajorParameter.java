package com.fir.flow.entity.flow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;


/**
 * <p>
 * 充电舱方案表
 * </p>
 *
 * @author dpe
 * @since 2023-01-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ChargeStationScenario对象", description="充电舱方案表")
public class FlowMajorParameter implements Serializable {

    @ApiModelProperty(value = "流程任务id")
    private String taskId;

    @ApiModelProperty(value = "流程部署id")
    private String deployId;
}
