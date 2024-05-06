import axios from 'axios'
import Vue from "vue";
import router from '@/router'
//引入axios

// 动态获取本机ip，作为连接后台服务的地址,但访问地址不能是localhost
// 为了灵活配置后台地址，后期需要更改为，配置文件指定字段决定优先使用配置ip还是自己生产的ip（如下）
const hostPort = document.location.host;
const aa = hostPort.split(":")
const host = aa[0];
const whiteList = [
    "/auth/login",
    "/auth/logout",
]

//axios.create能创造一个新的axios实例
const server = axios.create({

    baseURL: "http" + "://" + host + ":10273", //配置请求的url
    timeout: 6000, //配置超时时间
    headers: {}//配置请求头
})


/** interceptors为axios的拦截器 如果我们想要在请求以前做些什么 这个时候就需要用到拦截器  视业务需求而定 **/

/** 请求拦截器 **/
server.interceptors.request.use(function (config) {
    // 非白名单的请求都加上一个请求头
    let key = true;
    whiteList.find(function (value) {
        if (value === config.url) {
            key = false;
        }
    });
    if (key) {
        let token = localStorage.getItem("token")
        // eslint-disable-next-line no-empty
        if (token !== null && token !== "") {
        }
    // 请求中增加token
        if (token) {
            config.headers.Authorization = token;
        }
    }

    return config;
}, function (error) {
    return Promise.reject(error);
});


/** 响应拦截器 **/
server.interceptors.response.use(function (response) {

    // 放置业务逻辑代码
    // response是服务器端返回来的数据信息，与Promise获得数据一致
    let data = response.data

    // (´～`) 数据权限失效的时候重新登录
    if(data.code === 401){
        Vue.prototype.$message.error(data.msg)
        localStorage.clear()
        router.replace({path: '/login'}).then(() => {})
    }
    return data;

}, function (error) {
    // axios请求服务器端发生错误的处理
    return Promise.reject(error);
});


/**
 * 定义一个函数-用于接口
 * 利用我们封装好的request发送请求
 * @param url 后台请求地址
 * @param method 请求方法（get/post...）
 * @param obj 向后端传递参数数据
 * @returns AxiosPromise 后端接口返回数据
 */
export function dataInterface(url, method, obj) {
    return server({
        url: url,
        method: method,
        params: obj
    })
}


/**
 * 下载文件
 *
 * @param url 后台请求地址
 * @param method 请求方法（get/post...）
 * @param obj 向后端传递参数数据
 * @returns AxiosPromise 后端接口返回数据
 */
export function downloadInterface(url, method, obj) {
    return server({
        url: url,
        method: method,
        params: obj,
        responseType: 'blob',
    })
}


/**
 * 上传文件
 *
 * @param url 后台请求地址
 * @param method 请求方法（get/post...）
 * @param formData 文件
 * @param obj 向后端传递参数数据
 * @returns AxiosPromise 后端接口返回数据
 */
export function uploadInterface(url, method, formData, obj) {
    return server({
        url: url,
        method: method,
        data: formData,
        params: obj,
        headers: {"Content-Type": "multipart/form-data"},
        timeout: 30000,
    })
}

/** get请求 **/
export function dataInterfaceGet(url, obj) {
    return dataInterface(url, 'get', obj)
}


/** post请求 **/
export function dataInterfacePost(url, obj) {
    return dataInterface(url, 'post', obj)
}

/** delete请求 **/
export function dataInterfaceDelete(url, obj) {
    return dataInterface(url, 'delete', obj)
}

/** download下载文件请求 **/
export function download(url, obj) {
    return downloadInterface(url, 'post', obj)
}

/** upload 上传文件请求 **/
export function upload(url, formData, obj) {
    return uploadInterface(url, 'post', formData, obj)
}


export default server
