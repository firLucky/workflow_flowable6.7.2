import {dataInterfacePost, dataInterfaceGet, download} from "@/libs/request";



export default {

    /** 流程定义-分页数据 **/
    definitionPageData(formData){
        return dataInterfaceGet(`/mt/flow/definition/list`, formData);
    },

    /** 流程定义-流程流转记录 **/
    getFlowRecordListData(formData){
        return dataInterfaceGet('/mt/flow/task/flow/record', formData)
    },

    /** 流程定义-通过流程实例id获取流程定义id-分页数据 **/
    getProcessId(formData){
        return dataInterfaceGet('/m/flow/definition/process/id', formData)
    },

    /** 流程定义-读取流程的对象信息**/
    getProcessVariables(formData){
        return dataInterfaceGet('/mt/flow/task/process/variables', formData)
    },

    /** 流程定义-读取流程的文件 **/
    getProcessVariablesFile(formData){
        return download('/m/flow/task/process/variables/file', formData)
    },

    /** 流程定义-审批任务 **/
    completeOpenProcessDeal(formData){
        return dataInterfacePost('/mt/flow/task/complete', formData)
    },

    /** 流程定义-驳回任务至上一层 **/
    taskRejectProcessDeal(formData){
        return dataInterfacePost('/mt/flow/task/reject', formData)
    },

    /** 流程定义-通过流程实例id获取流程部署id与流程任务id **/
    getProcessIdDeployIdByTaskId(data) {
        return dataInterfaceGet('/m/flow/definition/process/deploy/id', data)
    },

    /** 流程定义-驳回并取消任务 **/
    taskStopProcessDeal(formData){
        return dataInterfacePost('/mt/flow/task/stop/process', formData)
    },

    /** 流程定义-发起人撤销任务 **/
    taskRevokeProcessDeal(formData){
        return dataInterfacePost('/mt/flow/task/revoke/process', formData)
    },


    /** 任务管理-我的任务-申请流程-分页数据 **/
    getMyFlowTakeListData(formData) {
        return dataInterfaceGet(`/mt/flow/task/my/process`, formData);
    },

    /** 任务管理-我的任务-提交流程申请 **/
    submitFormProcessDeal(formData){
        return dataInterfacePost('/mt/flow/definition/start', formData)
    },

    /** 任务管理-代办任务-分页数据 **/
    getDealWithTakeListData(formData){
        return dataInterfaceGet('/mt/flow/task/todo/list', formData)
    },



    /** 任务管理-已办任务-分页数据 **/
    getAccomplishTakeListData(formData){
        return dataInterfaceGet('/mt/flow/task/finishedList', formData)
    },

    getFileInfoById(fileId){
        return dataInterfacePost('/file/info/file',{fileId})
    },

    getPdfById(fileId){
        return dataInterfacePost('/file/load/file',{fileId},"blob")
    },


    /** 加载下一节点审核人信息 **/
    getFlowKeyUserGroup(formData){
        return dataInterfaceGet('/mt/flow/view/preset/flow/key/user/group', formData)
    },


    /** 加载流程图 **/
    getProcessDefinitionBpmnXML(formData){
        return dataInterfaceGet('/mt/flow/definition/read/xml', formData)
    },


    /** 获取流程用户任务节点信息 **/
    getProcessDefinitionUserTask(formData){
        return dataInterfaceGet('/mt/flow/definition/flow/key/list', formData)
    },


    /** 获取流程用户任务节点-可绑定/已绑定 人员列表 **/
    presetFlowKeyUserList(formData){
        return dataInterfaceGet('/mt/flow/view/preset/flow/key/user/list', formData)
    },

    /** 绑定人员至流程节点 **/
    presetFlowKeyUserAdd(formData){
        return dataInterfacePost('/mt/flow/view/preset/flow/key/user/add', formData)
    },


    /** 流程节点-解绑人员 **/
    presetFlowKeyUserDelete(formData){
        return dataInterfacePost('/mt/flow/view/preset/flow/key/user/delete', formData)
    },

    /** 生成指定流程实例的高亮流程 **/
    getActivityList(formData){
        return dataInterfaceGet('/mt/flow/view/list', formData)
    },


    /** 获得指定流程实例的任务列表 **/
    getTaskListByProcessInstanceId(formData){
        return dataInterfaceGet('/mt/flow/view/list/process/instance/id', formData)
    },


    /** 地市调度建议申请单关联 **/
    suggestionApply(formData){
        return dataInterfacePost('/m/t/charge/station/suggestion/city/apply', formData)
    },
}
