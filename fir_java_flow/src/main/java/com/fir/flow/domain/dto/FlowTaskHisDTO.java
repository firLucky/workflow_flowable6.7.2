package com.fir.flow.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import java.io.Serializable;
import java.util.List;


/**
 * <p>流程任务历史流转记录<p>
 *
 * @author fir
 * @date 2021-04-03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel("流程任务历史流转记录")
public class FlowTaskHisDTO implements Serializable {

    @ApiModelProperty("流程任务历史流转记录")
    private List<FlowTaskDto> flowList;

    @ApiModelProperty("流程是否完成")
    private boolean finished;
}
