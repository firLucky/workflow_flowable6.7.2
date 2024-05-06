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
public class User {

    @ApiModelProperty("用户id")
    private String userid;

    @ApiModelProperty("用户账号")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("公司名称")
    private String orgName;

    @ApiModelProperty("部门名称")
    private String deptName;
}

