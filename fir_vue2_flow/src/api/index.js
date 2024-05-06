/**
 * HTTP 库
 * 存储所有请求
 */

/** 系统登录 **/
import login from "@/api/system/login"


/** 工作流引擎 **/
import TakeFlowApi from "@/api/flow/TakeFlowApi";


export default {
    ...login,
    ...TakeFlowApi,
}
