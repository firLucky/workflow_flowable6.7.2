<!--
    流程审批
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
                    <el-row class="row-bu">
                        <el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="handleComplete">审批</el-button>
                                <el-button type="danger" @click="handleReject">驳回</el-button>
                            </el-form-item>
                        </el-form-item>
                    </el-row>
                    <el-row>

                    </el-row>
                </el-form>
            </el-card>

            <!--  流程图显示-->
            <el-card class="box-card-bpmnXML" :body-style="{ height: '100%', margin: '0 0 0 6%' }" >

                <template v-slot:header>
                    <i class="el-icon-picture"></i>
                    <span>流程图</span>
                </template>

                <el-col class="box-body" :span="16" :offset="2">
                    <my-process-viewer key="designer" v-model="bpmnXML" v-bind="bpmnControlForm"
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
        <!--    审批正常流程-->
        <el-dialog title="审批流程" :visible.sync='completeOpen' width="30%" :before-close="closeCompleteOpen">
            <el-form ref="completeOpenObject" :model="completeOpenObject" label-width="80px">
                <el-form-item label="处理意见" prop="comment"
                              :rules="[{ required: true, message: '请输入处理意见', trigger: 'blur' }]">
                    <el-input type="textarea" v-model="completeOpenObject.comment" placeholder="请输入处理意见"
                              :autosize="{minRows: 5, maxRows: 5}"/>
                </el-form-item>

                <el-form-item label="选择审批人" label-width="120px" prop="state">
                    <el-select v-model="completeOpenObject.changeUserId" clearable placeholder="请选择审批人">
                        <el-option v-for="item in examinePersonList" :key="item.value" :label="item.label"
                                   :value="item.value" style="width: 80%;"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>


            <span slot="footer" class="dialog-footer">
                            <el-button @click="completeOpen = false">取 消</el-button>
                            <el-button type="primary" @click="taskComplete">确 定</el-button>
                        </span>
        </el-dialog>


        <!--驳回流程-->
        <el-dialog title="审批流程" :visible.sync='rejectOpen' width="30%" :before-close="closeHandleReject">
            <el-form ref="taskForm" :model="rejectObject" label-width="80px">
                <el-form-item label="驳回意见" prop="comment"
                              :rules="[{ required: true, message: '请输入意见', trigger: 'blur' }]">
                    <el-input style="width: 100%" v-model="rejectObject.comment" type="textarea" placeholder="请输入意见"
                              :autosize="{minRows: 5, maxRows: 5}"/>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                            <el-button @click="rejectOpen = false">取 消</el-button>
                            <el-button type="primary" @click="taskReject">确 定</el-button>
                        </span>
        </el-dialog>
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
            // 流程实例义id
            processInstanceId: "",
            // 流程部署id
            deployId: "",

            // 流程申请信息对象
            fromData: {},

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

            // 下一个节点审批人列表
            examinePersonList: [
                {
                    value: "",
                    label: "暂无数据"
                },
            ],

            // 审批弹窗
            completeOpen: false,
            // 驳回研弹窗
            rejectOpen: false,
            // 审批信息对象
            completeOpenObject: {
                comment: "",
                changeUserId: "",
            },

            // 驳回信息对象
            rejectObject: {
                comment: "",
            },
        }
    },

    async created() {
        // 根据接受的数值不同将显示不同的内容
        const currentTarget = this.$route.query;
        this.processInstanceId = currentTarget.processInstanceId;
        this.deployId = currentTarget.deployId;
        this.taskId = currentTarget.taskId;
        this.getDetail();
        this.processVariables();
        this.flowKeyUserGroup();
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
                const code = response.code;
                const msg = response.msg;
                if (code === 200) {
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
                } else {
                    this.$message.error(msg)
                }
            });
        },
        /** 获取流程的对象变量内容 */
        processVariables() {

            const params = {
                taskId: this.taskId,
            }

            // 提交流程申请时填写的表单存入了流程变量中后续任务处理时需要展示
            this.$http.getProcessVariables(params).then(res => {
                this.$refs.fromFunction.setFormValue(res.data);
            });
        },
        /** 返回页面 */
        goBack() {
            // 关闭当前标签页并返回上个页面
            this.$store.dispatch("tagsView/delView", this.$route);
            this.$router.go(-1)
        },

        /** 审批表单开启按钮 **/
        handleComplete() {
            this.completeOpen = true
            // 审批表单重新开启的时候，重置待选择
            this.completeOpenObject = {}
        },
        /** 驳回表单按钮 **/
        handleReject() {
            this.rejectOpen = true
        },

        /** 驳回并取消任务 **/
        taskReject() {
            const params = {
                comment: this.rejectObject.comment,
                processInstanceId: this.processInstanceId,
            }

            this.$http.taskStopProcessDeal(params).then(res => {
                let code = res.code

                if (code === 200) {
                    this.completeOpen = false
                    this.goBack()
                } else {
                    this.completeOpen = false
                    this.goBack()
                }
            })
        },

        /** 审批结果发送按钮 **/
        taskComplete() {
            const params = {
                comment: this.completeOpenObject.comment,
                processInstanceId: this.processInstanceId,
                taskId: this.taskId,
                changeUserId: this.completeOpenObject.changeUserId,
            }
            this.$http.completeOpenProcessDeal(params).then(res => {
                let code = res.code
                let msg = res.msg

                if (code === 200) {
                    this.completeOpen = false
                    this.goBack()
                } else {
                    this.$message.error(msg)
                }
            })
        },

        /** 审批表单关闭按钮 **/
        closeCompleteOpen() {
            this.completeOpen = false
        },
        /** 驳回表单关闭按钮 **/
        closeHandleReject() {
            this.rejectOpen = false
        },

        /** 获取下一节点-审核人员信息单 **/
        flowKeyUserGroup() {
            const params = {
                taskId: this.taskId,
            }
            this.$http.getFlowKeyUserGroup(params).then(res => {
                this.examinePersonList = res.data

            });
        }
    }
}
</script>

<style scoped>
.box-card {
    width: 100%;
    margin-bottom: 20px;
}
.row-bu{
    text-align: center
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