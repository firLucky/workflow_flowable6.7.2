package com.fir.flow.service.flowable;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fir.flow.common.exception.DescriptionException;
import com.fir.flow.config.result.AjaxResult;
import com.fir.flow.domain.vo.FlowProcDefDto;
import com.fir.flow.entity.flow.FlowMajorParameter;
import org.flowable.bpmn.model.UserTask;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author fir
 * @date 2022/10/28 15:50
 */
public interface IFlowDefinitionService {


    /**
     * 流程定义列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return 流程定义分页列表数据
     */
    Page<FlowProcDefDto> list(String name, Integer pageNum, Integer pageSize);


    /**
     * 流程定义列表
     *
     * @param processDefinitionId 待定
     * @return 流程定义分页列表数据
     */
    List<UserTask> flowKeyList(String processDefinitionId);


    /**
     * 获取流程定义id
     *
     * @param deployId 流程部署ID
     */
    HashMap<String, String> getProcessId(String deployId);


    /**
     * 获取获取流程部署id与流程任务id
     *
     * @param processId 流程实例id
     * @return 流程任务id与流程部署id
     */
    FlowMajorParameter getTaskIdDeployIdByProcessId(String processId);


    /**
     * 导入流程文件
     *
     * @param name 流程名称
     * @param category 流程分类
     * @param in .bpmn20.xml 文件流
     */
    void importFile(String name, String category, InputStream in);


    /**
     * 激活或挂起流程定义
     *
     * @param state    状态
     * @param deployId 流程部署ID
     */
    void updateState(Integer state, String deployId) throws DescriptionException;


    /**
     * 删除流程定义
     *
     * @param deployId 流程部署ID act_ge_bytearray 表中 deployment_id值
     */
    void delete(String deployId) throws DescriptionException;


    /**
     * 根据流程定义ID启动流程实例
     *
     * @param procDefId 流程定义id
     * @param variables 申请内容对象
     * @throws Exception 流程启动失败
     */
    void startProcessInstanceById(String procDefId, Map<String, Object> variables) throws Exception;



    /**
     * 读取xml
     *
     * @param deployId 流程定义id
     * @return xml文件字符串信息
     * @throws IOException 转换流程xml时异常
     */
    AjaxResult readXml(String deployId) throws IOException;
}
