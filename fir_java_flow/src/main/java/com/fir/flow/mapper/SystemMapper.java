package com.fir.flow.mapper;


import com.fir.flow.entity.user.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fir
 * @date 2022/10/4 23:42
 */
public interface SystemMapper {


    /**
     * 检验用户是否存在
     *
     * @param username 用户名称
     *
     * @return 0-不存在/1-存在
     */
    Integer checkUsername(String username);


    /**
     * 检验用户登录是否有效
     *
     * @param username 用户名称
     * @param password 用户密码
     *
     * @return 0-不存在/1-存在
     */
    User checkLogin(@Param("username") String username, @Param("password") String password);


    /**
     * 用户id查询用户对象
     *
     * @param userId 用户id
     *
     * @return 用户信息对象
     */
    User getUserByUserId(String userId);


    /**
     * 获取所有用户信息
     *
     * @return 用户信息集合
     */
    List<User> getUserList();


    /**
     * 查询指定FlowKey节点 可选审批人用户集合
     *
     * @return 用户信息集合
     */
    List<User> getUserListByFlowKey(String taskDefinitionKey);


    /**
     * 查询指定FlowKey 可设定用户集合
     *
     * @return 用户信息集合
     */
    List<User> getCanSetUserListByFlowKey(String taskDefinitionKey);
}
