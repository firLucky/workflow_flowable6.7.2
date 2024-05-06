package com.fir.flow.controller.flowable;

import com.fir.flow.common.annotation.MoreSerializeField;
import com.fir.flow.common.annotation.SerializeField;
import com.fir.flow.config.result.AjaxResult;
import com.fir.flow.config.result.AjaxResultCode;
import com.fir.flow.domain.dto.FlowTaskDto;
import com.fir.flow.domain.vo.FlowTaskVo;
import com.fir.flow.service.flowable.IFlowTaskService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * <p>工作流任务管理<p>
 *
 * @author fir
 * @date 2021-04-03
 */
@Slf4j
@Api(tags = "工作流流程任务管理")
@RestController
@RequestMapping("/mt/flow/task")
public class FlowTaskController {


    @Resource
    private IFlowTaskService flowTaskService;


    /**
     * 修改
     */
    @ApiOperation(value = "我发起的流程", response = FlowTaskDto.class)
    @GetMapping(value = "/my/process")
    @MoreSerializeField({
            @SerializeField(clazz = AjaxResult.class, includes = {"data", "data"}),
            @SerializeField(clazz = AjaxResult.class, excludes = {"data"})
    })
    public AjaxResult myProcess(@ApiParam(value = "当前页码", required = true) @RequestParam Integer pageNum,
                                @ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize) {
        return flowTaskService.myProcess(pageNum, pageSize);
    }


    @ApiOperation(value = "驳回并取消任务流程", response = FlowTaskDto.class)
    @PostMapping(value = "/stop/process")
    public AjaxResult stopProcess(String processInstanceId, String comment) {
        AjaxResult ajaxResult;

        boolean key =  flowTaskService.stopProcess(processInstanceId, comment);
        ajaxResult = key?AjaxResult.success():AjaxResult.error(AjaxResultCode.LOSE_FLOW_STOP);

        return ajaxResult;
    }


    @ApiOperation(value = "撤回流程", response = FlowTaskDto.class)
    @PostMapping(value = "/revoke/process")
    public AjaxResult revokeProcess(String processInstanceId) {
        AjaxResult ajaxResult;
        boolean key =  flowTaskService.revokeProcess(processInstanceId);
        ajaxResult = key?AjaxResult.success():AjaxResult.error(AjaxResultCode.LOSE_FLOW_STOP);

        return ajaxResult;
    }


    @ApiOperation(value = "获取待办列表", response = FlowTaskDto.class)
    @GetMapping(value = "/todo/list")
    public AjaxResult todoList(@ApiParam(value = "当前页码", required = true) @RequestParam Integer pageNum,
                               @ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize) {
        return flowTaskService.todoList(pageNum, pageSize);
    }


    @ApiOperation(value = "获取已办任务", response = FlowTaskDto.class)
    @GetMapping(value = "/finishedList")
    public AjaxResult finishedList(@ApiParam(value = "当前页码", required = true) @RequestParam Integer pageNum,
                                   @ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize) {
        return flowTaskService.finishedList(pageNum, pageSize);
    }


    @ApiOperation(value = "流程历史流转记录", response = FlowTaskDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "procInsId", value = "流程实例Id"),
            @ApiImplicitParam(name = "deployId", value = "流程定义id"),
    })
    @GetMapping(value = "/flow/record")
    public AjaxResult flowRecord(String procInsId, String deployId) {
        return AjaxResult.success(flowTaskService.flowRecord(procInsId, deployId));
    }


    @ApiOperation(value = "获取流程变量对象信息", response = FlowTaskDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "流程任务Id")
    })
    @GetMapping(value = "/process/variables")
    public AjaxResult processVariables(String taskId) {
        return flowTaskService.processVariables(taskId);
    }


    @ApiOperation(value = "审批任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "流程任务Id"),
            @ApiImplicitParam(name = "processInstanceId", value = "流程实例id"),
            @ApiImplicitParam(name = "comment", value = "审批意见"),
            @ApiImplicitParam(name = "changeUserId", value = "被指派审批人"),
    })
    @PostMapping(value = "/complete")
    public AjaxResult complete(@NotEmpty(message = "流程任务Id不能为空")String taskId,
                               @NotEmpty(message = "流程实例id")String processInstanceId,
                               @NotEmpty(message = "审批意见不能为空")String comment,
                               @NotEmpty(message = "被指派审批人不能为空")String changeUserId) {
        return flowTaskService.complete(taskId, processInstanceId, comment, changeUserId);
    }


    @ApiOperation(value = "驳回任务至上一层")
    @PostMapping(value = "/reject")
    public AjaxResult taskReject(FlowTaskVo flowTaskVo) {
        flowTaskService.taskReject(flowTaskVo);
        return AjaxResult.success();
    }


    @ApiOperation(value = "退回任务")
    @PostMapping(value = "/return")
    public AjaxResult taskReturn(@RequestBody FlowTaskVo flowTaskVo) {
        flowTaskService.taskReturn(flowTaskVo);
        return AjaxResult.success();
    }


    @ApiOperation(value = "获取所有可回退的节点")
    @PostMapping(value = "/returnList")
    public AjaxResult findReturnTaskList(@RequestBody FlowTaskVo flowTaskVo) {
        return flowTaskService.findReturnTaskList(flowTaskVo);
    }


    @ApiOperation(value = "删除任务")
    @DeleteMapping(value = "/delete")
    public AjaxResult delete(@RequestBody FlowTaskVo flowTaskVo) {
        flowTaskService.deleteTask(flowTaskVo);
        return AjaxResult.success();
    }


    @ApiOperation(value = "认领/签收任务")
    @PostMapping(value = "/claim")
    public AjaxResult claim(@RequestBody FlowTaskVo flowTaskVo) {
        flowTaskService.claim(flowTaskVo);
        return AjaxResult.success();
    }


    @ApiOperation(value = "取消认领/签收任务")
    @PostMapping(value = "/unClaim")
    public AjaxResult unClaim(@RequestBody FlowTaskVo flowTaskVo) {
        flowTaskService.unClaim(flowTaskVo);
        return AjaxResult.success();
    }


    @ApiOperation(value = "委派任务")
    @PostMapping(value = "/delegate")
    public AjaxResult delegate(@RequestBody FlowTaskVo flowTaskVo) {
        flowTaskService.delegateTask(flowTaskVo);
        return AjaxResult.success();
    }


    @ApiOperation(value = "转办任务")
    @PostMapping(value = "/assign")
    public AjaxResult assign(@RequestBody FlowTaskVo flowTaskVo) {
        flowTaskService.assignTask(flowTaskVo);
        return AjaxResult.success();
    }


    /**
     * 生成流程图
     *
     * @param processId 任务ID
     */
    @PostMapping("/diagram/{processId}")
    public void genProcessDiagram(HttpServletResponse response,
                                  @PathVariable("processId") String processId) {
        InputStream inputStream = flowTaskService.diagram(processId);
        OutputStream os = null;
        BufferedImage image;
        try {
            image = ImageIO.read(inputStream);
            response.setContentType("image/png");
            os = response.getOutputStream();
            if (image != null) {
                ImageIO.write(image, "png", os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取流程执行过程生成流程图
     *
     * @param procInsId 流程实例编号
     * @param executionId 任务执行编号
     */
    @ApiOperation("生成流程图图片")
    @PostMapping("/flow/viewer")
    public AjaxResult getFlowViewer(String procInsId, String executionId) {
        return flowTaskService.getFlowViewer(procInsId, executionId);
    }
}
