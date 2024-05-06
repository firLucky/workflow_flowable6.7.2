import {dataInterfaceGet} from "../system/MeunApi";


/** 加载流程图 **/
export function getProcessDefinitionBpmnXML(obj) {
    return dataInterfaceGet("/mt/flow/view/get-bpmn-xml", obj)
}
/** 加载流程图 **/
export function getActivityList(obj) {
    return dataInterfaceGet("/mt/flow/view/list", obj)
}


/**  **/
export function getProcessInstance(obj) {
    return dataInterfaceGet("/mt/flow/view/get", obj)
}


/** **/
export function getTaskListByProcessInstanceId(obj) {
    return dataInterfaceGet("/mt/flow/view/list-by-process-instance-id", obj)
}
