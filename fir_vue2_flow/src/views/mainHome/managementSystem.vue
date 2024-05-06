<template>
    <div>
        <div class="mainTitle-box">
            <h1 class="mainTitle">工作流后台</h1>
        </div>
        <div class="div-navigation">
            <nav>
                <!--    此处在页面上显示一个标签，点击标签后，则会在当前网址上跳转to后面的值
                  导航连接实例
                -->
                <router-link id="logout" to="/login">登出</router-link>
            </nav>
        </div>

        <div class="about">
            <div class="management">
                <!--    左侧侧边栏数据-->
                <div class="sidebar">
                    <el-row class="tac">
                        <el-col class="elCol" :span="12" style="width: 245px; flex: 0 0 100%;">
                            <!--              设置路由跳转-->
                            <el-menu class="elMenuData"
                                     router
                                     :default-active="$route.path"
                            >
                                <el-menu-item index="">基础功能</el-menu-item>
<!--                                <el-submenu index="1">-->
<!--                                    <template #title>-->
<!--                                        <span>流程管理</span>-->
<!--                                    </template>-->
<!--                                    <el-menu-item-group>-->
<!--                                        <el-menu-item index="/flow/definition">流程定义</el-menu-item>-->
<!--                                        <el-menu-item index="/flow/my/process">我的流程</el-menu-item>-->
<!--                                        <el-menu-item index="/flow/my/process/from">表单配置</el-menu-item>-->

<!--                                    </el-menu-item-group>-->
<!--                                </el-submenu>-->

                                <el-submenu index="2">
                                    <template #title>
                                        <span>流程任务</span>
                                    </template>
                                    <el-menu-item-group>
                                        <el-menu-item index="/my/take/flow">我的任务</el-menu-item>
                                        <el-menu-item index="/take/deal/with">代办任务</el-menu-item>
                                        <el-menu-item index="/take/accomplish">已办任务</el-menu-item>
                                    </el-menu-item-group>
                                </el-submenu>
                                <el-submenu index="3">
                                    <template #title>
                                        <span>流程管理</span>
                                    </template>
                                    <el-menu-item-group>
                                        <el-menu-item index="/take/key/person">节点审批人设置</el-menu-item>
                                        <el-menu-item index="/take/msg">代办任务</el-menu-item>
                                    </el-menu-item-group>
                                </el-submenu>


                            </el-menu>
                        </el-col>
                    </el-row>
                </div>

                <div class="menuBack">
                    <router-view></router-view>
                </div>
            </div>
        </div>
    </div>
</template>

<script>

export default {
    name: "managementSystem",
}
</script>
<style scoped>

#logout{
    float: left;
    width: 70px;
}

.mainTitle-box {
    min-height: 20px;
    width: 100%;
    background-color: #303133;
    text-align: center;
}

/*主标题样式*/
.mainTitle {
    height: 100%;
    width: 100%;
    color: rgba(255, 255, 255, 0.73);
    margin: 0;
}

.div-navigation {
    background-color: #545C64;
    height: 10vh;
    width: 100%;
}


.about {
    height: calc(84vh);
    width: 100vm;
    background-color: rgb(83, 91, 100);
}


.elMenuData {
    width: 100%;
}

.management {
    height: 100%;
    width: 100%;
    background-color: #545C64;
    overflow: hidden; /*元素的裁剪显示*/
}


.sidebar {
    float: left;
    width: 251px;
    height: 100%;
    background-color: #545C64;
    overflow: auto; /*元素的裁剪显示*/
}

/*美化滚动条*/
.sidebar::-webkit-scrollbar {
    width: 5px;
    height: 5px;
}

.sidebar::-webkit-scrollbar-track {
    background-color: #F5F5F5;
}

.sidebar::-webkit-scrollbar-thumb {
    background-color: #c8c8c8;
}

/*编辑对话框css*/
.dialog-footer button:first-child {
    margin-right: 10px;
}

.menuBack {
    float: left;
    width: calc(100vw - 256px);
    height: 100%;
    background-color: #F5F5F5;
    overflow-x: hidden;
    overflow-y: auto;
    border-radius: 15px;
}

/*美化滚动条*/
.menuBack::-webkit-scrollbar {
    width: 5px;
    height: 5px;
}

.menuBack::-webkit-scrollbar-track {
    background-color: #F5F5F5;
}

.menuBack::-webkit-scrollbar-thumb {
    background-color: #c8c8c8;
}

</style>

