<template>

    <div class="back-container">
        节点审批人设置
        <el-table :data="tableData" border>
            <el-table-column label="流程任务标识" align="center" prop="id" width="480"/>
            <el-table-column label="任务名称" align="center" prop="name" width="280"/>

            <el-table-column label="操作" align="center" class-name="small-padding fixed-width" key="slot">

                <template #default="scope">
                    <el-button type="primary" size="mini" style="margin: 0 5px 0 0 ;"
                               @click="flowKeySelectPersonSelect(scope.row, 0)">
                        <i class="el-icon-tickets"></i>
                        候选人绑定
                    </el-button>

                    <el-button type="primary" size="mini" style="margin: 0 5px 0 0 ;"
                               @click="flowKeySelectPersonSelect(scope.row, 1)">
                        <i class="el-icon-tickets"></i>
                        候选人查看
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <!--  流程图显示-->
        <el-card class="box-card-bpmnXML" :body-style="{ height: '100%', margin: '0 0 0 6%' }" >

            <template v-slot:header>
                <i class="el-icon-picture"></i>
                <span>流程图</span>
            </template>

            <el-col class="box-body" :span="16" :offset="2">
                <my-process-viewer key="designer" v-model="bpmnXML" v-bind="bpmnControlForm"/>
            </el-col>
        </el-card>


        <!-- 可选用户列表 -->
        <el-dialog title="人员列表" :visible.sync='startProcessVisibleNewly' max-width="800px" min-width="30%" :before-close="cloNewly">
            <el-table :data="this.personList" height="63vh" border style="width: 100%;"
                      :header-cell-style="{textAlign:'center'}" :cell-style="{textAlign:'center'}">

                <el-table-column prop="name" label="姓名" :show-overflow-tooltip="true"/>
                <el-table-column prop="deptName" label="部门" :show-overflow-tooltip="true"/>
                <el-table-column prop="orgName" label="所属公司" :show-overflow-tooltip="true" width="180"/>
                <el-table-column class="tableButton" label="操作">
                    <!--                表按钮-->
                    <template #default="scope">
                        <div class="tableColumnButton">
                            <el-button v-if="startProcessVisibleNewlyFlowKeyType === 0" class="rightButton" link type="primary" icon="el-icon-edit-outline"
                                       @click="bindingPersonnel(scope.row, scope.$index)">绑定
                            </el-button>
                            <el-button v-if="startProcessVisibleNewlyFlowKeyType === 1" class="rightButton" link type="primary" icon="el-icon-edit-outline"
                                       @click="bindingPersonnelDelete(scope.row, scope.$index)">删除
                            </el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination @size-change="pageSizeDefinition" @current-change='pageNumDefinition'
                           :current-page="pageNum"
                           :page-sizes="[5, 10, 20, 50, 100]" :page-size="pageSize"
                           layout="total, sizes, prev, pager, next, jumper"
                           :total="total"></el-pagination>

        </el-dialog>
    </div>
</template>

<script>

import myProcessViewer from "@/views/bmp/ProcessViewer.vue";

export default {
    name: "flowKeySelectPerson",
    computed: {
    },

    components: {
        myProcessViewer
    },

    data() {
        return {
            // 流程定义id
            processDefinitionId: "",
            // 流程部署id
            deployId: "",

            // 流程列表数据
            tableData: [],

            /** 流程图显示 **/
            // BPMN 数据
            bpmnXML: null,
            bpmnControlForm: {
                prefix: "flowable"
            },


            startProcessVisibleNewly: false,
            startProcessVisibleNewlyFlowKey: "",

            /** 控制绑定界面，显示待绑定人员还是已绑定人员 **/
            startProcessVisibleNewlyFlowKeyType: -1,
            definitionTableData: [],

            /** 可选用户列表集合数据 **/
            personList: [],
            // 我的流程分页数据
            total: 10,  // 设置分页数据总个数，用于处理分页
            pageNum: 1,  // 默认页码，选则显示第几页数据
            pageSize: 10,  // 每页显示条目个数
            pageNumDefinition: 1,  // 默认页码，选则显示第几页数据
            pageSizeDefinition: 5,  // 每页显示条目个数，支持 v-model 双向绑定

            // 预置结构
            row: {
                userid: "",
            }
        }
    },


    async created() {
        const currentTarget = this.$route.query;
        this.deployId = currentTarget.deployId;
        this.processDefinitionId = currentTarget.processDefinitionId

        this.getFlowXmlByFlowKey();
        this.flowKeySelectPersonTableData();

    },

    methods: {


        /** 加载流程图 **/
        getFlowXmlByFlowKey() {
            const avb = {
                deployId: this.deployId
            }
            this.$http.getProcessDefinitionBpmnXML(avb).then(response => {
                this.bpmnXML = response.data

            });
        },


        /** 候选人绑定-候选人待绑定人员列表 **/
        flowKeySelectPersonSelect(row, type) {
            this.startProcessVisibleNewly = true
            this.startProcessVisibleNewlyFlowKey = row.id
            this.startProcessVisibleNewlyFlowKeyType = type
            this.getPersonList(row.id, type)
        },


        /** 流程-用户任务列表 **/
        flowKeySelectPersonTableData() {
            const params = {
                processDefinitionId: this.processDefinitionId
            }
            const date = this.$http.getProcessDefinitionUserTask(params)
            date.then(res => {
                this.tableData = res.data
            })
        },

        /** 查询当前可绑定人员列表 **/
        getPersonList(taskDefinitionKey, type){

            const params = {
                taskDefinitionKey: taskDefinitionKey,
                type: type,
            }

            this.$http.presetFlowKeyUserList(params).then(res => {
                this.personList = res.data
            });

        },

        /** 绑定人员至节点 **/
        bindingPersonnel(row, index){
            const params = {
                taskDefinitionKey: this.startProcessVisibleNewlyFlowKey,
                userId: row.userid
            }
            const date = this.$http.presetFlowKeyUserAdd(params)
            date.then(res => {
                let code = res.code;
                let msg = res.msg;
                if(code === 200){
                    this.$message.success("绑定成功")
                    this.personList.splice(index, 1);
                }else if(code === 400){
                    this.$message.error(msg)
                }
                else {
                    this.$message.error("绑定失败")
                }
            })
        },

        /** 人员-解绑 **/
        bindingPersonnelDelete(row, index){
            const params = {
                taskDefinitionKey: this.startProcessVisibleNewlyFlowKey,
                userId: row.userid
            }
            const date = this.$http.presetFlowKeyUserDelete(params)
            date.then(res => {
                let code = res.code;
                let msg = res.msg;
                if(code === 200){
                    this.$message.success("解绑成功")
                    this.personList.splice(index, 1);
                }else if(code === 400){
                    this.$message.error(msg)
                }
                else {
                    this.$message.error("解绑失败")
                }
            })
        },

        /** 表单对话框-叉号按钮 **/
        cloNewly() {
            this.startProcessVisibleNewly = false
            this.startProcessVisibleNewlyFlowKey = ""
        },
    },
}

</script>

<style scoped>

.box-body {
    width: 93%;
    height: 100%;
}

.box-card-bpmnXML{
    width: 100%;
    height: 70%;
    min-height: 757px;
}


.back-container{
    width: 100%;
    height: 100%;
}
</style>