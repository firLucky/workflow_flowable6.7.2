import {dataInterfacePost} from "@/libs/request";


export default {
    /** 系统登陆接口 **/
    login(obj) {
        return dataInterfacePost("/auth/login", obj)
    },


    /** 系统登出 **/
    logout(obj) {
        return dataInterfacePost("/auth/logout", obj)
    },


    /** 获取登录用户信息 **/
    userInfo(obj) {
        return dataInterfacePost("/auth/user/info", obj)
    },
}


