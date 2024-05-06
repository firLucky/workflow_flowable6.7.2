package com.fir.flow.entity.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "用户信息实体")
public class UserInfo {

    @ApiModelProperty("昵称")
    private String petName;

    @ApiModelProperty("用户头像(url)")
    private String userImg;
}
