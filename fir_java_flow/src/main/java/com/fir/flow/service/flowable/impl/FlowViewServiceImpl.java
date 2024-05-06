package com.fir.flow.service.flowable.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fir.flow.common.exception.DescriptionException;
import com.fir.flow.config.result.AjaxResult;
import com.fir.flow.config.result.AjaxResultCode;
import com.fir.flow.domain.dto.FlowCommentDto;
import com.fir.flow.domain.dto.FlowTaskDto;
import com.fir.flow.domain.dto.FlowTaskHisDTO;
import com.fir.flow.entity.common.DropDown;
import com.fir.flow.entity.flow.*;
import com.fir.flow.entity.user.User;
import com.fir.flow.entity.user.UserFlowKey;
import com.fir.flow.factory.FlowServiceFactory;
import com.fir.flow.mapper.SystemMapper;
import com.fir.flow.mapper.UserFlowKeyMapper;
import com.fir.flow.service.flowable.IFlowTaskService;
import com.fir.flow.service.flowable.IFlowViewService;
import com.fir.flow.utils.DateToolUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;


/**
 * @author fir
 * @date 2021-04-03
 **/
@Service
@Slf4j
public class FlowViewServiceImpl extends FlowServiceFactory implements IFlowViewService {


    /**
     * 流程任务接口
     */
    @Resource
    private IFlowTaskService iFlowTaskService;


    /**
     * 用户组织信息
     */
    @Resource
    private SystemMapper systemMapper;

    /**
     * 用户组织信息
     */
    @Resource
    private UserFlowKeyMapper userFlowKeyMapper;


    @Override
    public String getProcessDefinitionBpmnXml(String id) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(id);
        if (bpmnModel == null) {
            return null;
        }
        BpmnXMLConverter converter = new BpmnXMLConverter();
        return StrUtil.utf8Str(converter.convertToXML(bpmnModel));
    }


    /**
     * 获得指定流程实例的活动实例列表
     *
     * @param processInstanceId 流程实例的编号
     * @return 活动实例列表
     */
    @Override
    public List<BpmActivityRespVO> getActivityListByProcessInstanceId(String processInstanceId) {
        List<HistoricActivityInstance> activityList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).list();

        List<BpmActivityRespVO> bpmActivityRespVOList = new ArrayList<>(activityList.size());
        for (HistoricActivityInstance historicActivityInstance : activityList) {

            BpmActivityRespVO bpmActivityRespVO = new BpmActivityRespVO();

            bpmActivityRespVO.setKey(historicActivityInstance.getActivityId());
            bpmActivityRespVO.setType(historicActivityInstance.getActivityType());
            bpmActivityRespVO.setStartTime(historicActivityInstance.getStartTime());
            bpmActivityRespVO.setEndTime(historicActivityInstance.getEndTime());
            bpmActivityRespVO.setTaskId(historicActivityInstance.getTaskId());
            bpmActivityRespVOList.add(bpmActivityRespVO);
        }
        return bpmActivityRespVOList;
    }


    /**
     * 获得指令流程实例的流程任务列表，包括所有状态的
     *
     * @param processInstanceId 流程实例Id
     * @param deployId          流程部署Id
     * @return 流程任务列表
     */
    @Override
    public List<BpmTaskRespVO> getTaskListByProcessInstanceId(
            String processInstanceId, String deployId) {

        // 获得任务列表
        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime().desc() // 创建时间倒序
                .list();
        if (CollUtil.isEmpty(tasks)) {
            return Collections.emptyList();
        }
        
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        FlowTaskHisDTO flowTaskHisDTO = iFlowTaskService.flowRecord(processInstanceId, deployId);

        List<FlowTaskDto> hisFlowList = flowTaskHisDTO.getFlowList();

        List<BpmActivityRespVO> bpmActivityRespVOList =
                getActivityListByProcessInstanceId(processInstanceId);

        // 获取流程发起人信息
        String startUserId = historicProcessInstance.getStartUserId();
        User startUser = systemMapper.getUserByUserId(startUserId);
        String startUserName = startUser != null ? startUser.getName():"";

        // 获取到流程信息后对申请详情进行二次赋值
        List<BpmTaskRespVO> resultLis = new ArrayList<>();
        LinkedHashMap<String, BpmTaskRespVO> bpmTaskRespVOLinkedHashMap = new LinkedHashMap<>(tasks.size());
        int num = 1;
        for (HistoricTaskInstance item : tasks) {
            String taskDefinitionKey = item.getTaskDefinitionKey();

            String taskUserId = item.getAssignee();
            FlowTaskDto flowTaskDto = hisFlowList.get(num - 1);
            BpmActivityRespVO bpmActivityRespVO = bpmActivityRespVOList.get(num - 1);
            BpmTaskRespVO.User user = new BpmTaskRespVO.User();
            user.setId(taskUserId);
//            部门编号暂无不添加，实际可用组织机构编号
//            user.setDeptId(2L);
            user.setDeptName(flowTaskDto.getDeptName());
            user.setNickname(flowTaskDto.getAssigneeName());
            BpmTaskTodoPageItemRespVO.ProcessInstance bpmTaskTodoPageItemRespVO = new BpmTaskTodoPageItemRespVO.ProcessInstance();
            bpmTaskTodoPageItemRespVO.setId(processInstanceId);
            bpmTaskTodoPageItemRespVO.setName(historicProcessInstance.getProcessDefinitionName());
            bpmTaskTodoPageItemRespVO.setProcessDefinitionId(processInstanceId);
            bpmTaskTodoPageItemRespVO.setStartUserId(startUserId);
            bpmTaskTodoPageItemRespVO.setStartUserNickname(startUserName);
            BpmTaskRespVO bpmTaskRespVO = new BpmTaskRespVO();
            bpmTaskRespVO.setAssigneeUser(user);
            bpmTaskRespVO.setProcessInstance(bpmTaskTodoPageItemRespVO);
            bpmTaskRespVO.setName(flowTaskDto.getDeptName());
            bpmTaskRespVO.setClaimTime(DateToolUtils.strFormatDate(flowTaskDto.getFinishTime()));
            bpmTaskRespVO.setCreateTime(DateToolUtils.strFormatDate(flowTaskDto.getCreateTime()));
            bpmTaskRespVO.setDefinitionKey(bpmActivityRespVO.getKey());
            bpmTaskRespVO.setTaskDefinitionKey(taskDefinitionKey);

            // 处理审批意见
            FlowCommentDto flowCommentDto = flowTaskDto.getComment();
            String comment = "";
            String type;
            int typeInt = 1;
            if (flowCommentDto != null) {
                type = flowCommentDto.getType();
                comment = flowCommentDto.getComment();
                type = type != null ? type : "1";
                try {
                    typeInt = Integer.parseInt(type);
                } catch (NumberFormatException e) {
                    log.error("[{}] 流程节点状态参数处理失败", type);
                }
                // 因前后端参数不一致,现转化为前端参数
                switch (typeInt) {
                    case 1:
                        typeInt = 2;
                        break;
                    case 2:
                        typeInt = 1;
                        break;
                    default:
                }
                bpmTaskRespVO.setResult(typeInt);
            } else {
                // 当审批处于最后一个节点的时候,显示审批中的蓝色图标标记1
                bpmTaskRespVO.setResult(1);
            }

            bpmTaskRespVO.setReason(comment);
            // id作为主要区别参数,用id进行节点的定位
            bpmTaskRespVO.setId(item.getId());
            resultLis.add(bpmTaskRespVO);

            num++;
        }

        Collections.reverse(resultLis);
        for (BpmTaskRespVO bpmTaskRespVO : resultLis) {
            String taskDefinitionKey = bpmTaskRespVO.getTaskDefinitionKey();
            if (bpmTaskRespVOLinkedHashMap.containsKey(taskDefinitionKey)) {
                BpmTaskRespVO item = bpmTaskRespVOLinkedHashMap.get(taskDefinitionKey);
                item.setCreateTime(bpmTaskRespVO.getClaimTime());
                item.setClaimTime(bpmTaskRespVO.getCreateTime());
                item.setResult(bpmTaskRespVO.getResult());
                item.setReason(bpmTaskRespVO.getReason());
            } else {

                bpmTaskRespVOLinkedHashMap.put(taskDefinitionKey, bpmTaskRespVO);
            }
        }

        return new ArrayList<>(bpmTaskRespVOLinkedHashMap.values());
    }


    /**
     * 获取指定流程节点的可选用户
     *
     * @return 集合(用户名称, 用户id)
     */
    @Override
    public List<DropDown> flowKeyUserGroup(String taskId) {
        List<DropDown> dropDowns = new ArrayList<>();

        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task != null) {
            String taskKey = task.getTaskDefinitionKey();

            // 获取当前任务的流程定义ID
            // 使用RuntimeService查询流程实例
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            String processDefinitionId = processInstance.getProcessDefinitionId();

            // 获取流程模型
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

            // 根据当前任务的TaskKey找到对应的FlowNode
            FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(taskKey);

            // 获取下一个用户任务的流程键列表(目前只适用，下一个节点为单个用户的时候)
            List<String> nextTaskKeys = new ArrayList<>();
            List<SequenceFlow> outgoingFlows = currentFlowNode.getOutgoingFlows();
            for (SequenceFlow sequenceFlow : outgoingFlows) {
                FlowElement flowElement = sequenceFlow.getTargetFlowElement();
                if (flowElement instanceof UserTask) {
                    nextTaskKeys.add(flowElement.getId());
                }
            }
            String taskDefinitionKey = "";
            if (nextTaskKeys.size() == 1) {
                taskDefinitionKey = nextTaskKeys.get(0);
            } else if (nextTaskKeys.size() > 1) {
                log.error("识别到多个任务节点键");
            }

            if (StringUtils.isNoneBlank(taskDefinitionKey)) {
                // 执行相关操作
                List<User> userLis = systemMapper.getUserListByFlowKey(taskDefinitionKey);
                for (User item : userLis) {
                    DropDown dropDown = DropDown.builder()
                            .label(item.getName())
                            .value(item.getUserid())
                            .build();
                    dropDowns.add(dropDown);
                }
            }else {
                log.info("未能捕获到下一节点的用户流程任务 {}", taskId);
            }
        } else {
            log.error("流程任务查询失败:任务节点不存在 {}", taskId);
        }

        return dropDowns;
    }


    /**
     * 预置-指定流程节点的可选用户
     *
     * @param taskDefinitionKey 流程任务定义键
     * @param type              0:可绑定/1:已绑定
     * @return 用户信息集合
     */
    @Override
    public List<User> presetFlowKeyUserGroupList(String taskDefinitionKey, Integer type) {
        List<User> userList = new ArrayList<>();

        switch (type) {
            case 0:
                userList = systemMapper.getCanSetUserListByFlowKey(taskDefinitionKey);
                break;
            case 1:
                userList = systemMapper.getUserListByFlowKey(taskDefinitionKey);
                break;
        }
        return userList;
    }


    /**
     * 节点审批人设置-为指定流程节点，添加用户
     *
     * @param taskDefinitionKey 流程任务定义键
     * @param userId            用户id
     * @return 用户信息集合
     */
    @Override
    public int presetFlowKeyUserAdd(String taskDefinitionKey, String userId) {
        int state;
        UserFlowKey userFlowKey = new UserFlowKey();
        userFlowKey.setFlowKey(taskDefinitionKey);
        userFlowKey.setUserId(userId);

        // 判断当前节点是否已经绑定过该用户, 同一个节点（flowKey）不能多次绑定同一个人（userId）
        LambdaQueryWrapper<UserFlowKey> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserFlowKey::getFlowKey, taskDefinitionKey);
        lqw.eq(UserFlowKey::getUserId, userId);
        List<UserFlowKey> list = userFlowKeyMapper.selectList(lqw);

        if (list.size() == 0) {
            state = userFlowKeyMapper.insert(userFlowKey);
        } else {
            throw new DescriptionException(AjaxResultCode.ALREADY_BOUND);
        }

        return state;
    }


    /**
     * 节点审批人设置-为指定流程节点，删除用户
     *
     * @param taskDefinitionKey 流程任务定义键
     * @param userId            用户id
     * @return 用户信息集合
     */
    @Override
    public int presetFlowKeyUserDelete(String taskDefinitionKey, String userId) {
        UserFlowKey userFlowKey = new UserFlowKey();
        userFlowKey.setFlowKey(taskDefinitionKey);
        userFlowKey.setUserId(userId);

        // 判断当前节点是否已经绑定过该用户, 同一个节点（flowKey）不能多次绑定同一个人（userId）
        LambdaQueryWrapper<UserFlowKey> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserFlowKey::getFlowKey, taskDefinitionKey);
        lqw.eq(UserFlowKey::getUserId, userId);
        return userFlowKeyMapper.delete(lqw);
    }
}