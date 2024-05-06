package com.fir.flow.service.flowable.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fir.flow.common.enums.FlowComment;
import com.fir.flow.common.exception.CustomException;
import com.fir.flow.config.result.AjaxResult;
import com.fir.flow.domain.dto.FlowCommentDto;
import com.fir.flow.domain.dto.FlowTaskDto;
import com.fir.flow.domain.dto.FlowTaskHisDTO;
import com.fir.flow.domain.dto.FlowViewerDto;
import com.fir.flow.domain.vo.FlowTaskVo;
import com.fir.flow.entity.flow.FlowMajorParameter;
import com.fir.flow.entity.user.User;
import com.fir.flow.factory.FlowServiceFactory;
import com.fir.flow.mapper.SystemMapper;
import com.fir.flow.service.flowable.IFlowDefinitionService;
import com.fir.flow.service.flowable.IFlowTaskService;
import com.fir.flow.utils.DateToolUtils;
import com.fir.flow.utils.flow.FlowableUtils;
import com.fir.flow.utils.system.UserInfoTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * @author fir
 * @date 2021-04-03
 **/
@Service
@Slf4j
public class FlowTaskServiceImpl extends FlowServiceFactory implements IFlowTaskService {


    /**
     * 用户信息 deo层
     */
    @Resource
    private SystemMapper systemMapper;


    /**
     * 流程定义接口
     */
    @Resource
    private IFlowDefinitionService iFlowDefinitionService;


    /**
     * 审批任务/审批任务并更改下一个节点的任务人
     *
     * @param taskId            任务id
     * @param processInstanceId 流程实例id
     * @param comment           审批意见
     * @param changeUserId      被指派审批人
     * @return 成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AjaxResult complete(String taskId, String processInstanceId, String comment, String changeUserId) {
        // 流程变量信息（暂未使用）
        Map<String, Object> values = null;
        String userId = UserInfoTools.userId();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            return AjaxResult.error("任务不存在");
        }

//        Task实例的getDelegationState()方法返回任务委派状态，返回类型为TaskDelegationState枚举类型，可能的值为：
//        PENDING：任务未被委派
//        RESOLVED：任务已被委派并已解决
//        PENDING_AND_FORCED：任务已被强制委派，但仍未解决
//        RESOLVED_AND_FORCED：任务已被强制委派，并已解决
        if (DelegationState.PENDING.equals(task.getDelegationState())) {
            taskService.addComment(taskId, processInstanceId, FlowComment.DELEGATE.getType(), comment);
            taskService.resolveTask(taskId, values);
        } else {
            taskService.addComment(taskId, processInstanceId, FlowComment.NORMAL.getType(), comment);

            taskService.setAssignee(taskId, userId);
            taskService.complete(taskId, values);
        }

        // 可以从候选组中选择下一个审批人
        if (StringUtils.isNotBlank(changeUserId)) {
            FlowMajorParameter flowMajorParameter = iFlowDefinitionService.getTaskIdDeployIdByProcessId(processInstanceId);

            taskService.setAssignee(flowMajorParameter.getTaskId(), changeUserId);
        }

        return AjaxResult.success();
    }


    /**
     * 驳回任务至上一层
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    @Override
    public void taskReject(FlowTaskVo flowTaskVo) {
        if (taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult().isSuspended()) {
            throw new CustomException("任务处于挂起状态!");
        }
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // 获取全部节点列表，包含子节点
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // 获取当前任务节点元素
        FlowElement source = null;
        if (allElements != null) {
            for (FlowElement flowElement : allElements) {
                // 类型为用户节点
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    // 获取节点信息
                    source = flowElement;
                }
            }
        }

        // 目的获取所有跳转到的节点 targetIds
        // 获取当前节点的所有父级用户任务节点
        // 深度优先算法思想：延边迭代深入
        List<UserTask> parentUserTaskList = FlowableUtils.iteratorFindParentUserTasks(source, null, null);
        if (parentUserTaskList == null || parentUserTaskList.size() == 0) {
            throw new CustomException("当前节点为初始任务节点，不能驳回");
        }
        // 获取活动 ID 即节点 Key
        List<String> parentUserTaskKeyList = new ArrayList<>();
        parentUserTaskList.forEach(item -> parentUserTaskKeyList.add(item.getId()));
        // 获取全部历史节点活动实例，即已经走过的节点历史，数据采用开始时间升序
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).orderByHistoricTaskInstanceStartTime().asc().list();
        // 数据清洗，将回滚导致的脏数据清洗掉
        List<String> lastHistoricTaskInstanceList = FlowableUtils.historicTaskInstanceClean(allElements, historicTaskInstanceList);
        // 此时历史任务实例为倒序，获取最后走的节点
        List<String> targetIds = new ArrayList<>();
        // 循环结束标识，遇到当前目标节点的次数
        int number = 0;
        StringBuilder parentHistoricTaskKey = new StringBuilder();
        for (String historicTaskInstanceKey : lastHistoricTaskInstanceList) {
            // 当会签时候会出现特殊的，连续都是同一个节点历史数据的情况，这种时候跳过
            if (parentHistoricTaskKey.toString().equals(historicTaskInstanceKey)) {
                continue;
            }
            parentHistoricTaskKey = new StringBuilder(historicTaskInstanceKey);
            if (historicTaskInstanceKey.equals(task.getTaskDefinitionKey())) {
                number++;
            }
            // 在数据清洗后，历史节点就是唯一一条从起始到当前节点的历史记录，理论上每个点只会出现一次
            // 在流程中如果出现循环，那么每次循环中间的点也只会出现一次，再出现就是下次循环
            // number == 1，第一次遇到当前节点
            // number == 2，第二次遇到，代表最后一次的循环范围
            if (number == 2) {
                break;
            }
            // 如果当前历史节点，属于父级的节点，说明最后一次经过了这个点，需要退回这个点
            if (parentUserTaskKeyList.contains(historicTaskInstanceKey)) {
                targetIds.add(historicTaskInstanceKey);
            }
        }


        // 目的获取所有需要被跳转的节点 currentIds
        // 取其中一个父级任务，因为后续要么存在公共网关，要么就是串行公共线路
        UserTask oneUserTask = parentUserTaskList.get(0);
        // 获取所有正常进行的任务节点 Key，这些任务不能直接使用，需要找出其中需要撤回的任务
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        // 需驳回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runTaskList 比对，获取需要撤回的任务
        List<UserTask> currentUserTaskList = FlowableUtils.iteratorFindChildUserTasks(oneUserTask, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> currentIds.add(item.getId()));


        // 规定：并行网关之前节点必须需存在唯一用户任务节点，如果出现多个任务节点，则并行网关节点默认为结束节点，原因为不考虑多对多情况
        if (targetIds.size() > 1 && currentIds.size() > 1) {
            throw new CustomException("任务出现多对多情况，无法撤回");
        }

        // 循环获取那些需要被撤回的节点的ID，用来设置驳回原因
        List<String> currentTaskIds = new ArrayList<>();
        currentIds.forEach(currentId -> runTaskList.forEach(runTask -> {
            if (currentId.equals(runTask.getTaskDefinitionKey())) {
                currentTaskIds.add(runTask.getId());
            }
        }));
        // 设置驳回意见
        currentTaskIds.forEach(item -> taskService.addComment(item, task.getProcessInstanceId(), FlowComment.REJECT.getType(), flowTaskVo.getComment()));

        try {
            // 如果父级任务多于 1 个，说明当前节点不是并行节点，原因为不考虑多对多情况
            if (targetIds.size() > 1) {
                // 1 对 多任务跳转，currentIds 当前节点(1)，targetIds 跳转到的节点(多)
                runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId()).moveSingleActivityIdToActivityIds(currentIds.get(0), targetIds).changeState();
            }
            // 如果父级任务只有一个，因此当前任务可能为网关中的任务
            if (targetIds.size() == 1) {
                // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetIds.get(0) 跳转到的节点(1)
                runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId()).moveActivityIdsToSingleActivityId(currentIds, targetIds.get(0)).changeState();
            }
        } catch (FlowableObjectNotFoundException e) {
            throw new CustomException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new CustomException("无法取消或开始活动");
        }

    }


    /**
     * 退回任务
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void taskReturn(FlowTaskVo flowTaskVo) {
        if (taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult().isSuspended()) {
            throw new CustomException("任务处于挂起状态");
        }
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // 获取全部节点列表，包含子节点
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // 获取当前任务节点元素
        FlowElement source = null;
        // 获取跳转的节点元素
        FlowElement target = null;
        if (allElements != null) {
            for (FlowElement flowElement : allElements) {
                // 当前任务节点元素
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    source = flowElement;
                }
                // 跳转的节点元素
                if (flowElement.getId().equals(flowTaskVo.getTargetKey())) {
                    target = flowElement;
                }
            }
        }

        // 从当前节点向前扫描
        // 如果存在路线上不存在目标节点，说明目标节点是在网关上或非同一路线上，不可跳转
        // 否则目标节点相对于当前节点，属于串行
        Boolean isSequential = FlowableUtils.iteratorCheckSequentialReferTarget(source, flowTaskVo.getTargetKey(), null, null);
        if (!isSequential) {
            throw new CustomException("当前节点相对于目标节点，不属于串行关系，无法回退");
        }


        // 获取所有正常进行的任务节点 Key，这些任务不能直接使用，需要找出其中需要撤回的任务
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        // 需退回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runTaskList 比对，获取需要撤回的任务
        List<UserTask> currentUserTaskList = FlowableUtils.iteratorFindChildUserTasks(target, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> currentIds.add(item.getId()));

        // 循环获取那些需要被撤回的节点的ID，用来设置驳回原因
        List<String> currentTaskIds = new ArrayList<>();
        currentIds.forEach(currentId -> runTaskList.forEach(runTask -> {
            if (currentId.equals(runTask.getTaskDefinitionKey())) {
                currentTaskIds.add(runTask.getId());
            }
        }));
        // 设置回退意见
        currentTaskIds.forEach(currentTaskId -> taskService.addComment(currentTaskId, task.getProcessInstanceId(), FlowComment.RE_BACK.getType(), flowTaskVo.getComment()));

        try {
            // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetKey 跳转到的节点(1)
            runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId()).moveActivityIdsToSingleActivityId(currentIds, flowTaskVo.getTargetKey()).changeState();
        } catch (FlowableObjectNotFoundException e) {
            throw new CustomException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new CustomException("无法取消或开始活动");
        }
    }


    /**
     * 获取所有可回退的节点
     *
     * @param flowTaskVo 工作流任务-请求参数
     * @return UserTask 可回退节点集合
     */
    @Override
    public AjaxResult findReturnTaskList(FlowTaskVo flowTaskVo) {
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息，暂不考虑子流程情况
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        Collection<FlowElement> flowElements = process.getFlowElements();
        // 获取当前任务节点元素
        UserTask source = null;
        if (flowElements != null) {
            for (FlowElement flowElement : flowElements) {
                // 类型为用户节点
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    source = (UserTask) flowElement;
                }
            }
        }
        // 获取节点的所有路线
        List<List<UserTask>> roads = FlowableUtils.findRoad(source, null, null, null);
        // 可回退的节点列表
        List<UserTask> userTaskList = new ArrayList<>();
        for (List<UserTask> road : roads) {
            if (userTaskList.size() == 0) {
                // 还没有可回退节点直接添加
                userTaskList = road;
            } else {
                // 如果已有回退节点，则比对取交集部分
                userTaskList.retainAll(road);
            }
        }
        return AjaxResult.success(userTaskList);
    }


    /**
     * 删除任务
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    @Override
    public void deleteTask(FlowTaskVo flowTaskVo) {
        // todo 待确认删除任务是物理删除任务 还是逻辑删除，让这个任务直接通过？
//        taskService.deleteTask(flowTaskVo.getTaskId(), flowTaskVo.getComment());
    }


    /**
     * 认领/签收任务
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claim(FlowTaskVo flowTaskVo) {
//        taskService.claim(flowTaskVo.getTaskId(), flowTaskVo.getUserId());
    }


    /**
     * 取消认领/签收任务
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unClaim(FlowTaskVo flowTaskVo) {
//        taskService.unclaim(flowTaskVo.getTaskId());
    }


    /**
     * 委派任务
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(FlowTaskVo flowTaskVo) {
//        taskService.delegateTask(flowTaskVo.getTaskId(), flowTaskVo.getAssignee());
    }


    /**
     * 转办任务
     *
     * @param flowTaskVo 工作流任务-请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTask(FlowTaskVo flowTaskVo) {
//        taskService.setAssignee(flowTaskVo.getTaskId(), flowTaskVo.getComment());
    }


    /**
     * 我发起的流程
     *
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 流程数组
     */
    @Override
    public AjaxResult myProcess(Integer pageNum, Integer pageSize) {

        String userId = UserInfoTools.userId();
        Page<FlowTaskDto> page = new Page<>();

        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery().startedBy(userId).orderByProcessInstanceStartTime().desc();
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage(pageSize * (pageNum - 1), pageSize);
        page.setTotal(historicProcessInstanceQuery.count());
        List<FlowTaskDto> flowList = new ArrayList<>();
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            FlowTaskDto flowTask = new FlowTaskDto();
            flowTask.setCreateTime(DateToolUtils.dateFormatStr(hisIns.getStartTime()));
            flowTask.setFinishTime(DateToolUtils.dateFormatStr(hisIns.getEndTime()));
            flowTask.setProcInsId(hisIns.getId());

            // 计算耗时
            if (Objects.nonNull(hisIns.getEndTime())) {
                long time = hisIns.getEndTime().getTime() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            } else {
                long time = System.currentTimeMillis() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            }
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(hisIns.getProcessDefinitionId()).singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setCategory(pd.getCategory());
            flowTask.setProcDefVersion(pd.getVersion());
            // 当前所处流程 赋值任务节点 审批人
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).list();
            if (CollectionUtils.isNotEmpty(taskList)) {
                Task task = taskList.get(0);
                flowTask.setTaskId(task.getId());
                flowTask.setTaskName(task.getName());
                // 流程完成时，不在处理当前节点信息
                String finishTime = flowTask.getFinishTime();
                if (!StringUtils.isNoneBlank(finishTime)) {
                    String userIdTask = task.getAssignee();
                    User user = systemMapper.getUserByUserId(userIdTask);

                    flowTask.setAssigneeName(user.getName());
                    flowTask.setDeptName(user.getDeptName());
                }
            } else {
                List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
                HistoricTaskInstance historicTask = historicTaskInstance.get(0);
                flowTask.setTaskId(historicTask.getId());

                // 流程完成时，不在处理当前节点信息
                String finishTime = flowTask.getFinishTime();
                if (!StringUtils.isNoneBlank(finishTime)) {
                    String userIdTask = historicTask.getAssignee();
                    User user = systemMapper.getUserByUserId(userIdTask);

                    flowTask.setAssigneeName(user.getName());
                    flowTask.setDeptName(user.getDeptName());
                }
            }
            flowList.add(flowTask);
        }
        page.setRecords(flowList);
        return AjaxResult.success(page);
    }


    /**
     * 驳回并取消任务流程
     *
     * @param processInstanceId 流程实例id
     * @return 成功/失败
     */
    @Override
    public boolean stopProcess(String processInstanceId, String comment) {
        List<Task> task = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (CollectionUtils.isEmpty(task)) {
            throw new CustomException("流程未启动或已执行完成，取消申请失败");
        }
        // 获取当前需撤回的流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        if (Objects.nonNull(bpmnModel)) {
            Process process = bpmnModel.getMainProcess();
            List<EndEvent> endNodes = process.findFlowElementsOfType(EndEvent.class, false);
            if (CollectionUtils.isNotEmpty(endNodes)) {
                String userId = UserInfoTools.userId();
                Authentication.setAuthenticatedUserId(userId);

                Task task1 = task.get(0);
                comment = StringUtils.isNotBlank(comment) ? comment : FlowComment.CANCEL_USER.getRemark();
                taskService.addComment(task1.getId(), processInstance.getProcessInstanceId(), FlowComment.CANCEL_USER.getType(), comment);

                // 获取当前流程最后一个节点
                String endId = endNodes.get(0).getId();
                List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstance.getProcessInstanceId()).list();
                List<String> executionIds = new ArrayList<>();
                executions.forEach(execution -> executionIds.add(execution.getId()));
                // 变更流程为已结束状态
                runtimeService.createChangeActivityStateBuilder().moveExecutionsToSingleActivityId(executionIds, endId).changeState();
            }
        }

        return true;
    }


    /**
     * 撤回流程  目前存在错误
     *
     * @param processInstanceId 流程实例id
     * @return 成功/失败
     */
    @Override
    public boolean revokeProcess(String processInstanceId) {
        String userId = UserInfoTools.userId();
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if (task == null) {
            throw new CustomException("流程未启动或已执行完成，无法撤回");
        }
        // 检查当前用户是否为流程的发起人
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance == null || !userId.equals(processInstance.getStartUserId())) {
            throw new CustomException("只有流程发起人才能撤销流程");
        }
        // 委托任务给自己
        taskService.delegateTask(task.getId(), userId);

        // 完成任务
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        if (Objects.nonNull(bpmnModel)) {
            Process process = bpmnModel.getMainProcess();
            List<EndEvent> endNodes = process.findFlowElementsOfType(EndEvent.class, false);
            if (CollectionUtils.isNotEmpty(endNodes)) {

                // 此处想添加说明表示是由用户取消申请，但目前有问题
                taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), FlowComment.CANCEL.getType(), FlowComment.CANCEL.getRemark());
                // 获取当前流程最后一个节点
                String endId = endNodes.get(0).getId();
                List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstance.getProcessInstanceId()).list();
                List<String> executionIds = new ArrayList<>();
                executions.forEach(execution -> executionIds.add(execution.getId()));
                // 变更流程为已结束状态
                runtimeService.createChangeActivityStateBuilder().moveExecutionsToSingleActivityId(executionIds, endId).changeState();
            }
        }
        return true;
    }


    /**
     * 待办任务列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return 待办任务列表
     */
    @Override
    public AjaxResult todoList(Integer pageNum, Integer pageSize) {

        String userId = UserInfoTools.userId();
        Page<FlowTaskDto> page = new Page<>();

        TaskQuery taskQuery = taskService.createTaskQuery().active().includeProcessVariables().taskAssignee(userId).orderByTaskCreateTime().desc();
        page.setTotal(taskQuery.count());
        List<Task> taskList = taskQuery.listPage(pageSize * (pageNum - 1), pageSize);
        List<FlowTaskDto> flowList = new ArrayList<>();
        for (Task task : taskList) {
            FlowTaskDto flowTask = new FlowTaskDto();
            // 当前流程信息
            flowTask.setTaskId(task.getId());
            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
            flowTask.setCreateTime(DateToolUtils.dateFormatStr(task.getCreateTime()));
            flowTask.setProcDefId(task.getProcessDefinitionId());
            flowTask.setExecutionId(task.getExecutionId());
            flowTask.setTaskName(task.getName());
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());

            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            String taskUserId = historicProcessInstance.getStartUserId();
            User user = systemMapper.getUserByUserId(taskUserId);
            if (user != null) {
                flowTask.setStartUserId(user.getUserid());
                flowTask.setStartUserName(user.getName());
                flowTask.setStartDeptName(user.getDeptName());
            } else {
                flowTask.setStartUserId("无数据");
                flowTask.setStartUserName("无数据");
                flowTask.setStartDeptName("无数据");
            }
            flowList.add(flowTask);
        }

        page.setRecords(flowList);
        return AjaxResult.success(page);
    }


    /**
     * 已办任务列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return 已办任务列表
     */
    @Override
    public AjaxResult finishedList(Integer pageNum, Integer pageSize) {
        Page<FlowTaskDto> page = new Page<>();
        String userId = UserInfoTools.userId();
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskAssignee(userId).orderByHistoricTaskInstanceEndTime().desc();
        List<HistoricTaskInstance> historicTaskInstanceList = taskInstanceQuery.listPage(pageSize * (pageNum - 1), pageSize);
        List<FlowTaskDto> hisTaskList = Lists.newArrayList();
        for (HistoricTaskInstance histTask : historicTaskInstanceList) {
            FlowTaskDto flowTask = new FlowTaskDto();
            // 当前流程信息
            flowTask.setTaskId(histTask.getId());
            // 审批人员信息
            flowTask.setCreateTime(DateToolUtils.dateFormatStr(histTask.getCreateTime()));
            flowTask.setFinishTime(DateToolUtils.dateFormatStr(histTask.getEndTime()));
            flowTask.setDuration(getDate(histTask.getDurationInMillis()));
            flowTask.setProcDefId(histTask.getProcessDefinitionId());
            flowTask.setTaskDefKey(histTask.getTaskDefinitionKey());
            flowTask.setTaskName(histTask.getName());

            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(histTask.getProcessDefinitionId()).singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(histTask.getProcessInstanceId());
            flowTask.setHisProcInsId(histTask.getProcessInstanceId());

            // 流程发起人信息
            String userIdTask = histTask.getAssignee();
            User user = systemMapper.getUserByUserId(userIdTask);

            flowTask.setAssigneeName(user.getName());
            flowTask.setDeptName(user.getDeptName());
            flowTask.setStartUserId(user.getUserid());
            flowTask.setStartUserName(user.getName());
            flowTask.setStartDeptName(user.getDeptName());
            hisTaskList.add(flowTask);
        }
        page.setTotal(taskInstanceQuery.count());
        page.setRecords(hisTaskList);
        Map<String, Object> result = new HashMap<>();
        result.put("result", page);
        result.put("finished", true);
        return AjaxResult.success(page);
    }




    /**
     * 流程历史流转记录
     *
     * @param procInsId 流程实例Id
     * @param deployId  流程定义id
     * @return 流程历史流转记录
     */
    @Override
    public FlowTaskHisDTO flowRecord(String procInsId, String deployId) {
        FlowTaskHisDTO flowTaskHisDTO = new FlowTaskHisDTO();

        if (StringUtils.isNotBlank(procInsId)) {
            List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId).orderByHistoricActivityInstanceStartTime().desc().list();
            List<FlowTaskDto> hisFlowList = new ArrayList<>();
            for (HistoricActivityInstance histIns : list) {
                if (StringUtils.isNotBlank(histIns.getTaskId())) {
                    FlowTaskDto flowTask = new FlowTaskDto();
                    flowTask.setTaskId(histIns.getTaskId());
                    flowTask.setTaskName(histIns.getActivityName());
                    flowTask.setCreateTime(DateToolUtils.dateFormatStr(histIns.getStartTime()));
                    flowTask.setFinishTime(DateToolUtils.dateFormatStr(histIns.getEndTime()));
                    if (StringUtils.isNotBlank(histIns.getAssignee())) {
                        String userId = histIns.getAssignee();

                        User user = systemMapper.getUserByUserId(userId);
                        flowTask.setAssigneeId(userId);
                        flowTask.setAssigneeName(user.getName());
                        flowTask.setDeptName(user.getDeptName());
                    }
                    // 展示审批人员
                    List<HistoricIdentityLink> linksForTask = historyService.getHistoricIdentityLinksForTask(histIns.getTaskId());
                    StringBuilder stringBuilder = new StringBuilder();
                    for (HistoricIdentityLink identityLink : linksForTask) {
                        // 获选人,候选组/角色(多个)
                        if ("candidate".equals(identityLink.getType())) {
                            if (StringUtils.isNotBlank(identityLink.getUserId())) {
//                                SysUser sysUser = sysUserService.selectUserById(Long.parseLong(identityLink.getUserId()));
//                                stringBuilder.append(sysUser.getNickName()).append(",");
                            }
                            if (StringUtils.isNotBlank(identityLink.getGroupId())) {
//                                SysRole sysRole = sysRoleService.selectRoleById(Long.parseLong(identityLink.getGroupId()));
//                                stringBuilder.append(sysRole.getRoleName()).append(",");
                            }
                        }
                    }
                    if (StringUtils.isNotBlank(stringBuilder)) {
                        flowTask.setCandidate(stringBuilder.substring(0, stringBuilder.length() - 1));
                    }

                    flowTask.setDuration(histIns.getDurationInMillis() == null || histIns.getDurationInMillis() == 0 ? null : getDate(histIns.getDurationInMillis()));
                    // 获取意见评论内容
                    log.info("{}  {}", histIns.getActivityName(), histIns.getProcessInstanceId());
                    List<Comment> commentList = taskService.getProcessInstanceComments(histIns.getProcessInstanceId());
                    log.info("{}  {}", commentList, "\n");
                    commentList.forEach(comment -> {
                        if (histIns.getTaskId().equals(comment.getTaskId())) {
                            flowTask.setComment(FlowCommentDto.builder().type(comment.getType()).comment(comment.getFullMessage()).build());
                        }
                    });
                    hisFlowList.add(flowTask);
                }
            }
            flowTaskHisDTO.setFlowList(hisFlowList);
            // 查询当前任务是否完成
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
            flowTaskHisDTO.setFinished(CollectionUtils.isNotEmpty(taskList));
        }
        // 第一次申请获取初始化表单(该部分适用于将表单数据放置与后台的形式，本项目不使用该方式)
//        if (StringUtils.isNotBlank(deployId)) {
//            SysForm sysForm = sysInstanceFormService.selectSysDeployFormByDeployId(deployId);
//            if (Objects.isNull(sysForm)) {
//                return AjaxResult.error("请先配置流程表单");
//            }
//            map.put("formData", JSONObject.parseObject(sysForm.getFormContent()));
//        }
        return flowTaskHisDTO;
    }


    /**
     * 获取流程过程图
     *
     * @param processId 流程实例id
     * @return 图片文件流
     */
    @Override
    public InputStream diagram(String processId) {
//        String processDefinitionId;
//        // 获取当前的流程实例
//        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
//        // 如果流程已经结束，则得到结束节点
//        if (Objects.isNull(processInstance)) {
//            HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
//
//            processDefinitionId = pi.getProcessDefinitionId();
//        } else {// 如果流程没有结束，则取当前活动节点
//            // 根据流程实例ID获得当前处于活动状态的ActivityId合集
//            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
//            processDefinitionId = pi.getProcessDefinitionId();
//        }
//
//        // 获得活动的节点
//        List<HistoricActivityInstance> highLightedFlowList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).orderByHistoricActivityInstanceStartTime().asc().list();
//
//        List<String> highLightedFlows = new ArrayList<>();
//        List<String> highLightedNodes = new ArrayList<>();
//        //高亮线
//        for (HistoricActivityInstance tempActivity : highLightedFlowList) {
//            if ("sequenceFlow".equals(tempActivity.getActivityType())) {
//                //高亮线
//                highLightedFlows.add(tempActivity.getActivityId());
//            } else {
//                //高亮节点
//                highLightedNodes.add(tempActivity.getActivityId());
//            }
//        }
//
//        //获取流程图
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
//        ProcessEngineConfiguration configuration = processEngine.getProcessEngineConfiguration();
//        //获取自定义图片生成器
//        ProcessDiagramGenerator diagramGenerator = new CustomProcessDiagramGenerator();
//        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedNodes, highLightedFlows, configuration.getActivityFontName(),
//                configuration.getLabelFontName(), configuration.getAnnotationFontName(), configuration.getClassLoader(), 1.0, true);
        return new ByteArrayInputStream(new byte[0]);

    }


    /**
     * 获取流程执行过程
     *
     * @param procInsId 流程实例编号
     * @param executionId 任务执行编号
     * @return 流程执行过程
     */
    @Override
    public AjaxResult getFlowViewer(String procInsId, String executionId) {
        List<FlowViewerDto> flowViewerList = new ArrayList<>();
        FlowViewerDto flowViewerDto;
        // 获取任务开始节点(临时处理方式)
        List<HistoricActivityInstance> startNodeList = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId).orderByHistoricActivityInstanceStartTime().asc().listPage(0, 3);
        for (HistoricActivityInstance startInstance : startNodeList) {
            if (!"sequenceFlow".equals(startInstance.getActivityType())) {
                flowViewerDto = new FlowViewerDto();
                if (!"sequenceFlow".equals(startInstance.getActivityType())) {
                    flowViewerDto.setKey(startInstance.getActivityId());
                    // 根据流程节点处理时间校验改节点是否已完成
                    flowViewerDto.setCompleted(!Objects.isNull(startInstance.getEndTime()));
                    flowViewerList.add(flowViewerDto);
                }
            }
        }
        // 历史节点
        List<HistoricActivityInstance> hisActIns = historyService.createHistoricActivityInstanceQuery().executionId(executionId).orderByHistoricActivityInstanceStartTime().asc().list();
        for (HistoricActivityInstance activityInstance : hisActIns) {
            if (!"sequenceFlow".equals(activityInstance.getActivityType())) {
                flowViewerDto = new FlowViewerDto();
                flowViewerDto.setKey(activityInstance.getActivityId());
                // 根据流程节点处理时间校验改节点是否已完成
                flowViewerDto.setCompleted(!Objects.isNull(activityInstance.getEndTime()));
                flowViewerList.add(flowViewerDto);
            }
        }
        return AjaxResult.success(flowViewerList);
    }


    /**
     * 获取流程变量
     *
     * @param taskId 流程任务Id
     * @return 流程信息变量
     */
    @Override
    public AjaxResult processVariables(String taskId) {
        // 流程变量
        // 流程变量
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskId(taskId).singleResult();
        if (Objects.nonNull(historicTaskInstance)) {
            return AjaxResult.success(historicTaskInstance.getProcessVariables());
        } else {
            Map<String, Object> variables = taskService.getVariables(taskId);
            return AjaxResult.success(variables);
        }
    }


    /**
     * 流程完成时间处理
     *
     * @param ms 时间戳
     * @return 完成时间
     */
    private String getDate(long ms) {

        long day = ms / (24 * 60 * 60 * 1000);
        long hour = (ms / (60 * 60 * 1000) - day * 24);
        long minute = ((ms / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long second = (ms / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);

        if (day > 0) {
            return day + "天" + hour + "小时" + minute + "分钟";
        }
        if (hour > 0) {
            return hour + "小时" + minute + "分钟";
        }
        if (minute > 0) {
            return minute + "分钟";
        }
        if (second > 0) {
            return second + "秒";
        } else {
            return 0 + "秒";
        }
    }
}