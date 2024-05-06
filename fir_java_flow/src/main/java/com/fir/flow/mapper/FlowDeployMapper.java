package com.fir.flow.mapper;


import com.fir.flow.domain.vo.FlowProcDefDto;
import com.fir.flow.entity.flow.FlowMajorParameter;
import java.util.List;


/**
 * 流程定义查询
 *
 * @author fir
 * @email
 * @date 2022/1/29 5:44 下午
 **/
public interface FlowDeployMapper {

    /**
     * 流程定义列表
     * @param name
     * @return
     */
    List<FlowProcDefDto> selectDeployList(String name);


    /**
     * 获取一个流程的流程定义id
     *
     * @param deployId 流程部署ID
     * @return 流程定义id
     */
    String getProcessId(String deployId);


    /**
     * 通过流程任务id获取流程实例id与流程部署id
     *
     * @param processId 流程实例id
     * @return 流程任务id与流程部署id
     */
    FlowMajorParameter getProcessIdDeployIdByTaskId(String processId);
}
