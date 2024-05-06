package com.fir.flow.service.flowable.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fir.flow.common.constant.ProcessConstants;
import com.fir.flow.common.enums.FlowComment;
import com.fir.flow.common.exception.DescriptionException;
import com.fir.flow.config.result.AjaxResult;
import com.fir.flow.config.result.AjaxResultCode;
import com.fir.flow.domain.vo.FlowProcDefDto;
import com.fir.flow.entity.flow.FlowMajorParameter;
import com.fir.flow.factory.FlowServiceFactory;
import com.fir.flow.mapper.FlowDeployMapper;
import com.fir.flow.service.flowable.IFlowDefinitionService;
import com.fir.flow.utils.system.UserInfoTools;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * @author fir
 * @date 2022/10/28 15:51
 */
@Service
public class FlowDefinitionServiceImpl extends FlowServiceFactory implements IFlowDefinitionService {

    private static final String BPMN_FILE_SUFFIX = ".bpmn";


    @Resource
    private FlowDeployMapper flowDeployMapper;


    /**
     * 流程定义列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return 流程定义分页列表数据
     */
    @Override
    public Page<FlowProcDefDto> list(String name, Integer pageNum, Integer pageSize) {

        Page<FlowProcDefDto> page = new Page<>();
        PageHelper.startPage(pageNum, pageSize);
        final List<FlowProcDefDto> dataList = flowDeployMapper.selectDeployList(name);
        PageInfo<FlowProcDefDto> pageInfo = new PageInfo<>(dataList);
        page.setTotal(pageInfo.getTotal());
        page.setRecords(dataList);
        return page;
    }



    /**
     * 流程定义列表
     *
     * @param processDefinitionId 流程定义id
     * @return 流程定义分页列表数据
     */

    @Override
    public List<UserTask> flowKeyList(String processDefinitionId){
        List<UserTask> userTaskList = new ArrayList<>();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                String nodeId = userTask.getId();
                String nodeName = userTask.getName();
                UserTask i = new UserTask();
                i.setId(nodeId);
                i.setName(nodeName);

                userTaskList.add(i);
            }
        }

        return userTaskList;
    }

    /**
     * 获取流程定义id
     *
     * @param deployId 流程部署ID
     */
    @Override
    public HashMap<String, String> getProcessId(String deployId){

        String processId = flowDeployMapper.getProcessId(deployId);
        HashMap<String, String> map = new HashMap<>(1);
        map.put("processId", processId);
        return map;
    }


    /**
     * 获取获取流程部署id与流程任务id
     *
     * @param processId 流程实例id
     * @return 流程任务id与流程部署id
     */
    @Override
    public FlowMajorParameter getTaskIdDeployIdByProcessId(String processId){

        FlowMajorParameter flowMajorParameter = flowDeployMapper.getProcessIdDeployIdByTaskId(processId);
        // 当前所处流程,获取takeId
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processId).list();
        if (CollectionUtils.isNotEmpty(taskList)) {
            Task task = taskList.get(0);
            flowMajorParameter.setTaskId(task.getId());
        } else {
            List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(processId).orderByHistoricTaskInstanceEndTime().desc().list();
            flowMajorParameter.setTaskId(historicTaskInstance.get(0).getId());
        }
        return flowMajorParameter;
    }


    /**
     * 导入流程文件
     *
     * @param name 流程名称
     * @param category 流程分类
     * @param in .bpmn20.xml 文件流
     */
    @Override
    public void importFile(String name, String category, InputStream in) {
        Deployment deploy = repositoryService.createDeployment()
                .addInputStream(name + BPMN_FILE_SUFFIX, in)
                .name(name)
                .category(category)
                .deploy();
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploy.getId())
                .singleResult();
        repositoryService.setProcessDefinitionCategory(definition.getId(), category);
    }


    /**
     * 激活或挂起流程定义
     *
     * @param state    状态
     * @param deployId 流程部署ID
     */
    @Override
    public void updateState(Integer state, String deployId) throws DescriptionException {
        ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        // 激活
        if (state == 1) {
            try {
                repositoryService.activateProcessDefinitionById(procDef.getId(), true, null);
            } catch (FlowableException e) {
                throw new DescriptionException("激活流程失败");
            }
        }
        // 挂起
        if (state == 2) {
            try {
                repositoryService.suspendProcessDefinitionById(procDef.getId(), true, null);
            } catch (FlowableException e) {
                throw new DescriptionException("挂起流程失败");
            }
        }
    }


    /**
     * 删除流程定义
     *
     * @param deployId 流程部署ID act_ge_bytearray 表中 deployment_id值
     */
    @Override
    public void delete(String deployId) throws DescriptionException{
        // true 允许级联删除 ,不设置会导致数据库外键关联异常
        try {
            repositoryService.deleteDeployment(deployId, true);
        }catch (FlowableObjectNotFoundException e){
            throw new DescriptionException("无法删除流程");
        }
    }

    /**
     * 读取xml
     *
     * @param deployId 流程定义id
     * @return xml文件字符串信息
     */
    @Override
    public AjaxResult readXml(String deployId) throws IOException {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(definition.getDeploymentId(), definition.getResourceName());
        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        return AjaxResult.success(AjaxResultCode.SUCCESS, result);
    }


    /**
     * 根据流程定义ID启动流程实例
     *
     * @param procDefId 流程定义id
     * @param variables 申请内容对象
     * @throws Exception 流程启动失败
     */
    @Override
    public void startProcessInstanceById(String procDefId, Map<String, Object> variables) throws Exception{
        String name = UserInfoTools.userName();
        String userId = UserInfoTools.userId();

        // 如有流程中设置 ${day<5} 等判断条件,则 variables 中需要配置该参数不然会报错
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId)
                    .latestVersion().singleResult();
            if (Objects.nonNull(processDefinition) && processDefinition.isSuspended()) {
               throw new DescriptionException("流程已被挂起,请先激活流程");
            }

            // 设置流程发起人Id到流程中
            identityService.setAuthenticatedUserId(userId);
            variables.put(ProcessConstants.PROCESS_INITIATOR, "");
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId, variables);
            // 给第一步申请人节点设置任务执行人和意见 第一个节点不设置为申请人节点有点问题？
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
            if (Objects.nonNull(task)) {
                taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), FlowComment.NORMAL.getType(), name + "发起流程申请");
                taskService.setAssignee(task.getId(), userId);
                taskService.complete(task.getId(), variables);
            }
    }
}
