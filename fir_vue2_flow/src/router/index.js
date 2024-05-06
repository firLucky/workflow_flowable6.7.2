import Login from "@/views/loginPage";
import Vue from 'vue'   //引入Vue
import Router from 'vue-router'
import managementSystem from "@/views/mainHome/managementSystem";
import myTakeFlow from "@/views/flow/myTakeFlow";
import takeAccomplish from "@/views/flow/takeAccomplish";
import takeDealWith from "@/views/flow/takeDealWith";
import processApply from "@/views/flow/deal/processApply";
import processDetails from "@/views/flow/deal/processDetails";
import processDeal from "@/views/flow/deal/processDeal";
import keyPerson from "@/views/system/keyPerson.vue";
import flowKeySelectPerson from "@/views/system/flowKeySelectPerson.vue";

Vue.use(Router)  //Vue全局使用Router

const routes = [
    {
        path: '/',
        // 重定向到/login,
        redirect: "/login"
    },
    {
        path: '/login',
        // name: 'Hello',
        component: Login
    },
    {
        path: '/home',
        component: managementSystem,
        children: [        //子路由,嵌套路由 （此处偷个懒，免得单独再列一点）
            {
                path: '/my/take/flow',
                component: myTakeFlow
            },
            {
                path: '/take/deal/with',
                component: takeDealWith
            },
            {
                path: '/take/accomplish',
                component: takeAccomplish
            },
            {
                path: '/flow/my/process/submit',
                component: processApply

            },
            {
                path: '/flow/my/process/details',
                component: processDetails

            },
            {
                path: '/flow/my/process/deal',
                component: processDeal
            },
            {
                path: '/take/key/person',
                component: keyPerson
            },
            {
                path: '/take/key/person/select',
                component: flowKeySelectPerson
            },
        ]
    }

]

const originalPush = Router.prototype.push
Router.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

export default new Router({
    mode: 'history',  //去掉url中的#
    base: process.env.BASE_URL,
    routes: routes
})
