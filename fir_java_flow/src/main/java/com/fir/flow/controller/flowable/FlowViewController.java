package com.fir.flow.controller.flowable;

import com.fir.flow.config.result.AjaxResult;
import com.fir.flow.service.flowable.IFlowViewService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;


/**
 * <p>工作流任务管理<p>
 *
 * @author fir
 * @date 2021-04-03
 */
@Slf4j
@Api(tags = "工作流流程图接口")
@RestController
@RequestMapping("/mt/flow/view")
public class FlowViewController {


    @Resource
    private IFlowViewService iFlowViewService;


    @GetMapping("/get/bpmn/xml")
    @ApiOperation(value = "获得流程定义的 BPMN XML")
    @ApiImplicitParam(name = "id", value = "编号", required = true, example = "1024", dataTypeClass = String.class)

    public AjaxResult getProcessDefinitionBpmnXml(String id) {
        String bpmnXml = iFlowViewService.getProcessDefinitionBpmnXml(id);
        System.out.println(bpmnXml);
        return AjaxResult.success("操作成功", bpmnXml);
    }


    @GetMapping("/list")
    @ApiOperation(value = "生成指定流程实例的高亮流程图",
            notes = "只高亮进行中的任务。通过前端的 ProcessViewer.vue 界面的 highlightDiagram 方法")
    @ApiImplicitParam(name = "processInstanceId", value = "流程实例的编号", required = true, dataTypeClass = String.class)
    public AjaxResult getActivityList(String processInstanceId) {
        return AjaxResult.success(iFlowViewService.getActivityListByProcessInstanceId(processInstanceId));
    }


    @GetMapping("/list/process/instance/id")
    @ApiOperation(value = "获得指定流程实例的任务列表", notes = "包括完成的、未完成的")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstanceId", value = "流程实例id", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "deployId", value = "流程部署id", required = true, dataTypeClass = String.class),
    })

    public AjaxResult getTaskListByProcessInstanceId(String processInstanceId, String deployId) {
        return AjaxResult.success(iFlowViewService.getTaskListByProcessInstanceId(processInstanceId, deployId));
    }

    @ApiOperation("获取指定流程节点的可选用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "流程任务id"),
    })
    @GetMapping("/preset/flow/key/user/group")
    public AjaxResult flowKeyUserGroup(String taskId) {

        return AjaxResult.success(iFlowViewService.flowKeyUserGroup(taskId));
    }

    @ApiOperation("节点审批人设置-指定流程节点 可绑定用户列表/已绑定任务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskDefinitionKey", value = "流程任务定义键"),
            @ApiImplicitParam(name = "type", value = "0:可绑定/1:已绑定"),
    })
    @GetMapping("/preset/flow/key/user/list")
    public AjaxResult presetFlowKeyUserList(String taskDefinitionKey, Integer type) {

        return AjaxResult.success(iFlowViewService.presetFlowKeyUserGroupList(taskDefinitionKey, type));
    }

    @ApiOperation("节点审批人设置-为指定流程节点，添加用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskDefinitionKey", value = "流程任务定义键"),
            @ApiImplicitParam(name = "userId", value = "用户id"),
    })
    @PostMapping("/preset/flow/key/user/add")
    public AjaxResult presetFlowKeyUserAdd(String taskDefinitionKey, String userId) {

        return AjaxResult.success(iFlowViewService.presetFlowKeyUserAdd(taskDefinitionKey, userId));
    }

    @ApiOperation("节点审批人设置-为指定流程节点，删除用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskDefinitionKey", value = "流程任务定义键"),
            @ApiImplicitParam(name = "userId", value = "用户id"),
    })
    @PostMapping("/preset/flow/key/user/delete")
    public AjaxResult presetFlowKeyUserDelete(String taskDefinitionKey, String userId) {

        return AjaxResult.success(iFlowViewService.presetFlowKeyUserDelete(taskDefinitionKey, userId));
    }
}
