package com.fir.flow.controller.flowable;

import com.alibaba.fastjson.JSONObject;
import com.fir.flow.common.exception.DescriptionException;
import com.fir.flow.config.result.AjaxResult;
import com.fir.flow.config.result.AjaxResultCode;
import com.fir.flow.domain.vo.FlowProcDefDto;
import com.fir.flow.service.flowable.IFlowDefinitionService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;


/**
 * @author fir
 * @date 2022/10/28 15:49
 */
@Slf4j
@Api(tags = "流程定义")
@RestController
@RequestMapping("/mt/flow/definition")
public class FlowDefinitionController {

    @Resource
    private IFlowDefinitionService flowDefinitionService;


    @GetMapping(value = "/list")
    @ApiOperation(value = "流程定义列表", response = FlowProcDefDto.class)
    public AjaxResult list(@ApiParam(value = "当前页码", required = true, defaultValue = "1") @RequestParam Integer pageNum,
                       @ApiParam(value = "每页条数", required = true, defaultValue = "10") @RequestParam Integer pageSize,
                       @ApiParam(value = "流程名称") @RequestParam(required = false) String name) {
        return AjaxResult.success(flowDefinitionService.list(name, pageNum, pageSize));
    }

    @ApiOperation(value = "流程节点对象集合", response = FlowProcDefDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionId", value = "流程定义id"),
    })
    @GetMapping(value = "/flow/key/list")
    public AjaxResult flowKeyList(String processDefinitionId) {
        return AjaxResult.success(flowDefinitionService.flowKeyList(processDefinitionId));
    }


    @ApiOperation(value = "导入流程文件", notes = "上传bpmn20的xml文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "流程名称"),
            @ApiImplicitParam(name = "category", value = "流程分类")
    })
    @PostMapping("/import")
    public AjaxResult importFile(@RequestParam(required = false) String name,
                             @RequestParam(required = false) String category,
                             @RequestPart @RequestParam("file") MultipartFile file) {
        InputStream in = null;
        try {
            in = file.getInputStream();
            flowDefinitionService.importFile(name, category, in);
        } catch (Exception e) {
            log.error("导入失败:", e);
            return AjaxResult.error(e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("关闭输入流出错", e);
            }
        }

        return AjaxResult.success("导入成功");
    }


    @ApiOperation(value = "激活或挂起流程定义")
    @PostMapping(value = "/updateState")
    public AjaxResult updateState(@ApiParam(value = "1:激活,2:挂起", required = true) @RequestParam Integer state,
                              @ApiParam(value = "流程部署ID", required = true) @RequestParam String deployId) {
        AjaxResult result;
        try {
            flowDefinitionService.updateState(state, deployId);

            result = AjaxResult.success();
        } catch (DescriptionException e) {
            result = AjaxResult.error(e.getMessage());
        }

        return result;
    }


    @ApiOperation(value = "删除流程")
    @PostMapping(value = "/delete")
    public AjaxResult delete(String deployId) {
        AjaxResult result;
        try {
            flowDefinitionService.delete(deployId);
            result = AjaxResult.success();
        } catch (DescriptionException e) {
            result = AjaxResult.error(e.getMessage());
        }
        return result;
    }


    @ApiOperation(value = "读取xml文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deployId", value = "流程定义id"),
    })
    @GetMapping("/read/xml")
    public AjaxResult readXml(String deployId) {
        try {
            return flowDefinitionService.readXml(deployId);
        } catch (Exception e) {
            return AjaxResult.error("加载xml文件异常");
        }

    }


    /**
     * 修改
     */
    @ApiOperation(value = "根据流程定义id启动流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "procDefId", value = "流程定义id"),
            @ApiImplicitParam(name = "variables", value = "申请内容对象(URLEncoder.encode编码json)"),
    })
    @PostMapping("/start")
    public AjaxResult start(String processDefinitionId, String variables) {
        AjaxResult ajaxResult;
        try {
            variables = URLDecoder.decode(variables, "utf-8");
            Map<String, Object> objectMap = JSONObject.parseObject(variables);

            flowDefinitionService.startProcessInstanceById(processDefinitionId, objectMap);
            ajaxResult = AjaxResult.success(AjaxResultCode.SUCCESS_FLOW_START);
        } catch (UnsupportedEncodingException e) {
            ajaxResult = AjaxResult.error(AjaxResultCode.LOSE_FROM_DATA);
        }catch (Exception e){
            ajaxResult = AjaxResult.error(AjaxResultCode.LOSE_FLOW_START);
        }

        return ajaxResult;
    }
}
