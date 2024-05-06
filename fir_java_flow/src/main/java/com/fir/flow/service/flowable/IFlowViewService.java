package com.fir.flow.service.flowable;


import com.fir.flow.entity.common.DropDown;
import com.fir.flow.entity.flow.BpmActivityRespVO;
import com.fir.flow.entity.flow.BpmTaskRespVO;
import com.fir.flow.entity.user.User;
import java.util.List;


/**
 * @author fir
 * @date 2021-04-03 14:42
 */
public interface IFlowViewService {


    /**
     * 获得流程定义对应的 BPMN XML
     *
     * @param id 流程定义编号
     * @return BPMN XML
     */
    String getProcessDefinitionBpmnXml(String id);


    /**
     * 获得指定流程实例的活动实例列表
     *
     * @param processInstanceId 流程实例的编号
     * @return 活动实例列表
     */
    List<BpmActivityRespVO> getActivityListByProcessInstanceId(String processInstanceId);


    /**
     * 获得指令流程实例的流程任务列表，包括所有状态的
     *
     * @param processInstanceId 流程实例Id
     * @param deployId          流程部署Id
     * @return 流程任务列表
     */
    List<BpmTaskRespVO> getTaskListByProcessInstanceId(String processInstanceId, String deployId);


    /**
     * 获取指定流程节点的可选用户
     *
     * @param taskId 流程任务id
     * @return 集合(用户名称, 用户id)
     */
    List<DropDown> flowKeyUserGroup(String taskId);


    /**
     * 预置-指定流程节点的可选用户
     *
     * @param taskDefinitionKey 流程任务定义键
     * @param type              0:可绑定/1:已绑定
     * @return 用户信息集合
     */
    List<User> presetFlowKeyUserGroupList(String taskDefinitionKey, Integer type);


    /**
     * 节点审批人设置-为指定流程节点，添加用户
     *
     * @param taskDefinitionKey 流程任务定义键
     * @param userId            用户id
     * @return 用户信息集合
     */
    int presetFlowKeyUserAdd(String taskDefinitionKey, String userId);


    /**
     * 节点审批人设置-为指定流程节点，删除用户
     *
     * @param taskDefinitionKey 流程任务定义键
     * @param userId            用户id
     * @return 用户信息集合
     */
    int presetFlowKeyUserDelete(String taskDefinitionKey, String userId);
}
