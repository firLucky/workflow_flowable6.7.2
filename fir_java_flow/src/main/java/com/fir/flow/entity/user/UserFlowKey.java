package com.fir.flow.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author fir
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="用户表")
public class UserFlowKey {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @ApiModelProperty("流程任务key")
    private String flowKey;

    @ApiModelProperty("用户id")
    private String userId;
}

