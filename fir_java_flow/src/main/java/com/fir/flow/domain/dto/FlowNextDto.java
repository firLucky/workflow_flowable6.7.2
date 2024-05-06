package com.fir.flow.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 动态人员、组
 * @author fir
 * @date 2021/4/17 22:59
 */
@Data
public class FlowNextDto implements Serializable {

    private String type;

    private String vars;

//    private List<SysUser> userList;
//
//    private List<SysRole> roleList;
}
