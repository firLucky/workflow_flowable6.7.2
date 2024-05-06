<!--
    流程详情
-->
<template>

    <div class="back-container">
        <div class="app-container">

            <el-card class="box-card">
                <template v-slot:header>
                    <i class="el-icon-document"></i>
                    <span>基础信息</span>
                    <el-button style="float: right;" type="primary" @click="goBack">返回</el-button>
                </template>

                <el-form width="55%">
                    <fromInfo ref="fromFunction" :disabledKey=false></fromInfo>
                </el-form>
            </el-card>
            <!--  流程图显示-->
            <el-card class="box-card-bpmnXML" :body-style="{ height: '100%', margin: '0 0 0 6%' }" >

                <template v-slot:header>
                    <i class="el-icon-picture"></i>
                    <span>流程图</span>
                </template>

                <el-col class="box-body" :span="16" >
                    <my-process-viewer key="designer"  v-model="bpmnXML" v-bind="bpmnControlForm"
                                       :activityData="activityList" :taskData="tasks"/>
                </el-col>
            </el-card>

            <!--            审批记录记录-->
            <el-card class="box-card">
                <template v-slot:header>
                    <i class="el-icon-notebook-2"></i>
                    <span>审批记录</span>
                </template>
                <flowRecord :processInstanceId=this.processInstanceId></flowRecord>
            </el-card>

        </div>

    </div>
</template>

<script>
import fromInfo from "@/views/flow/form/fromInfo"
import myProcessViewer from '@/views/bmp/ProcessViewer'
import flowRecord from "@/views/flow/other/flowRecord";

export default {
    name: "processDeal",

    components: {
        fromInfo,
        myProcessViewer,
        flowRecord,
    },

    data() {
        return {
            currentTarget: "",

            // 流程实例id
            processInstanceId: "",
            // 流程部署id
            deployId: "",
            // 流程任务id
            taskId: "",

            /** 流程图显示 **/
            // BPMN 数据
            bpmnXML: null,
            bpmnControlForm: {
                prefix: "flowable"
            },

            // 流程节点高亮
            activityList: [],

            // 流程节点数据
            tasks: [],

        }
    },

    async created() {
        // 根据接受的数值不同将显示不同的内容
        const currentTarget = this.$route.query;
        this.processInstanceId = currentTarget.processInstanceId;
        this.deployId = currentTarget.deployId;
        this.taskId = currentTarget.taskId;

        this.getDetail();
        await this.processVariables();
    },

    methods: {

        getDetail() {
            // 加载流程图
            const avb = {
                deployId: this.deployId
            }
            this.$http.getProcessDefinitionBpmnXML(avb).then(response => {
                this.bpmnXML = response.data

            });

            // 加载流程节点高亮数据*/
            this.$http.getActivityList({
                processInstanceId: this.processInstanceId
            }).then(response => {
                this.activityList = response.data;
            });


            // 获取任务节点记录
            const TaskData = {
                processInstanceId: this.processInstanceId,
                deployId: this.deployId
            }
            this.$http.getTaskListByProcessInstanceId(TaskData).then(response => {
                // 任务节点记录
                this.tasks = [];
                // 移除已取消的审批
                response.data.forEach(task => {
                    if (task.result !== 4) {
                        this.tasks.push(task);
                    }
                });
                // 排序，将未完成的排在前面，已完成的排在后面；
                this.tasks.sort((a, b) => {
                    // 有已完成的情况，按照完成时间倒序*/
                    if (a.endTime && b.endTime) {
                        return b.endTime - a.endTime;
                    } else if (a.endTime) {
                        return 1;
                    } else if (b.endTime) {
                        return -1;
                        // 都是未完成，按照创建时间倒序
                    } else {
                        return b.createTime - a.createTime;
                    }
                });
            });


        },
        /** 获取流程的对象变量内容 */
        async processVariables() {

            const params = {
                taskId: this.taskId,
            }

            // 提交流程申请时填写的表单存入了流程变量中后续任务处理时需要展示
            await this.$http.getProcessVariables(params).then(res => {
                this.$refs.fromFunction.setFormValue(res.data);
            });
        },
        /** 返回页面 */
        goBack() {
            // 关闭当前标签页并返回上个页面
            this.$store.dispatch("tagsView/delView", this.$route);
            this.$router.go(-1)
        },
    }
    ,
}
</script>

<style scoped>
.box-card {
    width: 100%;
    margin-bottom: 20px;
}

.box-body {
    width: 93%;
    height: 100%;
}

.box-card-bpmnXML{
    width: 100%;
    height: 70%;
    min-height: 757px;
}
/*main-container全局样式*/
.app-container {
    height: 100%;
    padding: 20px;
}

.back-container{
    width: 100%;
    height: 100%;
}
</style>