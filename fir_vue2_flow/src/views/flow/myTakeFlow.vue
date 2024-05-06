<template>

    <div class="back-container">
        我的任务
        <el-row :gutter="10" class="opera-wrap">
            <el-col name="rightButtonel-clo" style="float: right;" :span="3">
                <div class="rightButton ">
                    <el-button icon="el-icon-plus" type="primary" size="mini" style="margin: 0px 5px 0 0 ;"
                               @click="startProcessKey">
                        发起申请
                    </el-button>
                </div>
            </el-col>
        </el-row>

        <!-- 表单设置 -->
        <el-table :data="tableData" border>
            <el-table-column label="流程编号" align="center" prop="procInsId" :show-overflow-tooltip="true"/>
            <el-table-column label="流程名称" align="center" prop="procDefName" :show-overflow-tooltip="true"/>
            <el-table-column label="流程类别" align="center" prop="category" width="100px"/>
            <el-table-column label="流程版本" align="center" width="80px">

                <template #default="scope">
                    <el-tag>v{{ scope.row.procDefVersion }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="提交时间" align="center" prop="createTime" width="180"/>
            <el-table-column label="流程状态" align="center" width="100">
                <template #default="scope">
                    <el-tag v-if="scope.row.finishTime == null || scope.row.finishTime === ''">进行中</el-tag>
                    <el-tag type="success" v-if="scope.row.finishTime != null && scope.row.finishTime !== ''">已完成
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="耗时" align="center" prop="duration" width="180"/>
            <el-table-column label="当前节点" align="center" prop="taskName"/>
            <el-table-column label="办理" align="center">
                <template #default="scope">
                    <label v-if="scope.row.assigneeName">{{ scope.row.assigneeName }}
                        <el-tag type="info">{{ scope.row.deptName }}</el-tag>
                    </label>
                    <label v-if="scope.row.candidate">{{ scope.row.candidate }}</label>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">

                <template slot-scope="scope">
                    <el-button type="primary" size="mini" style="margin: 0px 5px 0 0 ;"
                               @click="handleFlowRecord(scope.row)">
                        <i class="el-icon-tickets"></i>
                        详情
                    </el-button>
                    <el-button v-if="!scope.row.finishTime" type="primary" size="mini" style="margin: 0px 5px 0 0 ;"
                               @click="handleClose(scope.row)">
                        <i class="el-icon-close"></i>
                        取消
                    </el-button>

                </template>
            </el-table-column>
        </el-table>
        <el-pagination @size-change="btSizeMyTake" @current-change='btPageMyTake' :current-page="pageNum"
                       :page-sizes="[10, 20, 50, 100]" :page-size="pageSize"
                       layout="total, sizes, prev, pager, next, jumper"
                       :total="total"></el-pagination>

        <!--            流程列表表单-->
        <el-dialog title="流程列表" :visible.sync='startProcessVisibleNewly' width="576px" min-width="30%" :before-close="cloNewly">
            <el-table :data="this.definitionTableData" height="63vh" border style="width: 100%;"
                      :header-cell-style="{textAlign:'center'}" :cell-style="{textAlign:'center'}">

                <el-table-column prop="name" label="流程名称" :show-overflow-tooltip="true"/>
                <el-table-column prop="category" label="流程分组" :show-overflow-tooltip="true"/>
                <el-table-column class="tableButton" label="操作">
                    <!--                表按钮-->
                    <template #default="scope">
                        <div class="tableColumnButton">
                            <el-button class="rightButton" link type="primary" icon="el-icon-edit-outline"
                                       @click="startProcessMyFlow(scope.row)">发起流程
                            </el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination @size-change="btSizeDefinition" @current-change='btPageDefinition'
                           :current-page="pageNumDefinition"
                           :page-sizes="[5, 10, 20, 50, 100]" :page-size="pageSizeDefinition"
                           layout="total, sizes, prev, pager, next, jumper"
                           :total="totalDefinition"></el-pagination>

        </el-dialog>
    </div>
</template>

<script>

export default {
    name: "myTakeFlow",

    components: {
    },

    data() {
        return {
            // 我的申请流程列表数据
            tableData: [],

            // 流程列表
            definitionTableData: [],

            // 流程列表表单弹出框开关判断
            startProcessVisibleNewly: false,

            // 我的流程分页数据
            total: 10,  // 设置分页数据总个数，用于处理分页
            pageNum: 1,  // 默认页码，选则显示第几页数据
            pageSize: 10,  // 每页显示条目个数


            // 流程列表分页数据
            totalDefinition: 10,  // 设置分页数据总个数，用于处理分页
            pageNumDefinition: 1,  // 默认页码，选则显示第几页数据
            pageSizeDefinition: 5,  // 每页显示条目个数，支持 v-model 双向绑定
        }
    },


    async created() {
        await this.myTakePageData();

    },

    methods: {

        /** 表单对话框-叉号按钮 **/
        cloNewly() {
            this.startProcessVisibleNewly = false
        },

        /** 新增按钮 **/
        startProcessKey() {
            this.startProcessVisibleNewly = true
            this.definitionPageData();
        },


        /** 流程分页功能按钮 **/
        btSizeMyTake(val) {
            this.pageSize = val
            this.myTakePageData()
        },
        btPageMyTake(val) {
            this.pageNum = val
            this.myTakePageData()
        },


        /** 查询-获取基础分页查询数据 **/
        async myTakePageData() {
            // const tableData = []
            const params = {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
            };
            const date = this.$http.getMyFlowTakeListData(params)
            await date.then(res => {
                const returnData = res.data
                this.total = returnData.total
                this.tableData = returnData.records
            })
        },


        /** 流程分页功能按钮 **/
        btSizeDefinition(val) {
            this.pageSizeDefinition = val
            this.definitionPageData()
        },
        btPageDefinition(val) {
            this.pageNumDefinition = val
            this.definitionPageData()
        },


        /** 查询-获取流程分页查询数据 **/
        async definitionPageData() {

            const params = {
                pageNum: this.pageNumDefinition,
                pageSize: this.pageSizeDefinition,
            };
            const date = this.$http.definitionPageData(params)
            await date.then(res => {
                const returnData = res.data  // 后台返回的主要数

                this.totalDefinition = returnData.total
                this.definitionTableData = returnData.records
            })
        },


        /** 发起流程-获取流程任务列表 */
        startProcessMyFlow(row) {
            const processDefinitionId = row.id;
            const deployId = row.deploymentId;
            this.$router.push({
                path: '/flow/my/process/submit',
                query: {processDefinitionId: processDefinitionId, deployId: deployId, contentDomKey: true, flag: true}
            })

        },

        /** 查看已办任务的流程记录 **/
        handleFlowRecord(row) {

            const processInstanceId = row.procInsId;
            const deployId = row.deployId;
            const taskId = row.taskId;

            this.$router.push({
                path: '/flow/my/process/details',
                query: {
                    processInstanceId: processInstanceId,
                    deployId: deployId,
                    taskId: taskId
                }
            })
        },

        handleClose(row) {
            this.$confirm('此操作将取消流程申请, 是否继续?', '提示', {
                confirmButtonText: '是',
                cancelButtonText: '否',
                type: 'warning'
            }).then(() => {
                const params = {
                    processInstanceId: row.procInsId,
                }
                const date = this.$http.taskRevokeProcessDeal(params)
                date.then(res => {
                    const code = res.code
                    const msg = res.msg

                    if (code === 200) {

                        this.myTakePageData()
                        this.$message({
                            type: 'success',
                            message: '取消成功!'
                        });
                    } else {
                        this.$message({
                            type: 'error',
                            message: msg
                        });
                    }
                })
            })
        }
    },
}

</script>

<style scoped>
.elDialogFlowApply{
    width: 30%;
    /*min-width: ;*/
}
</style>