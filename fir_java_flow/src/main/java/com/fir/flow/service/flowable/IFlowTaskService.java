package com.fir.flow.service.flowable;

import com.fir.flow.config.result.AjaxResult;
import com.fir.flow.domain.dto.FlowTaskHisDTO;
import com.fir.flow.domain.vo.FlowTaskVo;
import java.io.InputStream;


/**
 * @author fir
 * @date 2021-04-03 14:42
 */
public interface IFlowTaskService {

    /**
     * 审批任务/审批任务并更改下一个节点的任务人
     *
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param comment 审批意见
     * @param changeUserId 被指派审批人
     * @return 成功/失败
     */
    AjaxResult complete(String taskId, String processInstanceId, String comment, String changeUserId);

    /**
     * 驳回任务至上一层
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    void taskReject(FlowTaskVo flowTaskVo);


    /**
     * 退回任务
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    void taskReturn(FlowTaskVo flowTaskVo);

    /**
     * 获取所有可回退的节点
     *
     * @param flowTaskVo 工作流任务-请求参数
     * @return UserTask 可回退节点集合
     */
    AjaxResult findReturnTaskList(FlowTaskVo flowTaskVo);

    /**
     * 删除任务
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    void deleteTask(FlowTaskVo flowTaskVo);

    /**
     * 认领/签收任务
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    void claim(FlowTaskVo flowTaskVo);

    /**
     * 取消认领/签收任务
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    void unClaim(FlowTaskVo flowTaskVo);

    /**
     * 委派任务
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    void delegateTask(FlowTaskVo flowTaskVo);

    /**
     * 转办任务
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    void assignTask(FlowTaskVo flowTaskVo);

    /**
     * 我发起的流程
     *
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 流程数组
     */
    AjaxResult myProcess(Integer pageNum, Integer pageSize);

    /**
     * 驳回并取消任务流程
     * @param processInstanceId 流程实例id
     * @return 成功/失败
     */
    boolean stopProcess(String processInstanceId, String comment);

    /**
     * 撤回流程  目前存在错误
     *
     * @param processInstanceId 流程实例id
     * @return 成功/失败
     */
    boolean revokeProcess(String processInstanceId);

    /**
     * 待办任务列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return 待办任务列表
     */
    AjaxResult todoList(Integer pageNum, Integer pageSize);

    /**
     * 已办任务列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return 已办任务列表
     */
    AjaxResult finishedList(Integer pageNum, Integer pageSize);

    /**
     * 流程历史流转记录
     *
     * @param procInsId 流程实例Id
     * @param deployId 流程定义id
     * @return 流程历史流转记录
     */
    FlowTaskHisDTO flowRecord(String procInsId, String deployId);

    /**
     * 获取流程过程图
     * @param processId 流程实例id
     * @return 图片文件流
     */
    InputStream diagram(String processId);

    /**
     * 获取流程执行过程
     *
     * @param procInsId 流程实例编号
     * @param executionId 任务执行编号
     * @return 流程执行过程
     */
    AjaxResult getFlowViewer(String procInsId,String executionId);

    /**
     * 获取流程变量
     *
     * @param taskId 流程任务Id
     * @return 流程信息变量
     */
    AjaxResult processVariables(String taskId);
}
