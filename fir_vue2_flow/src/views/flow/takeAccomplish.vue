<template>
    <div class="back-container">
        已办任务
        <!-- 表单设置 -->
        <el-table  :data="tableData" border>

            <el-table-column label="任务编号" align="center" prop="taskId" :show-overflow-tooltip="true"/>
            <el-table-column label="流程名称" align="center" prop="procDefName" :show-overflow-tooltip="true"/>
            <el-table-column label="任务节点" align="center" prop="taskName" />
            <el-table-column label="流程发起人" align="center">
                <template #default="scope">
                    <label>{{scope.row.startUserName}} <el-tag type="info">{{scope.row.startDeptName}}</el-tag></label>
                </template>
            </el-table-column>
            <el-table-column label="接收时间" align="center" prop="createTime" width="180"/>
            <el-table-column label="审批时间" align="center" prop="finishTime" width="180"/>
            <el-table-column label="耗时" align="center" prop="duration" width="180"/>
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-button
                        link
                        type="primary"
                        icon="el-icon-tickets"
                        @click="handleFlowRecord(scope.row)"
                    >流转记录</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination @size-change="handleSizeChange" @current-change='handleCyrRentChange' :current-page="pageNum"
                       :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                       :total="total"></el-pagination>
    </div>
</template>

<script>
export default {
    name: "takeAccomplish",

    components: {
    },

    data() {
        return {

            // 我的流程列表
            tableData: [],

            // 分页数据
            total: 10,  // 设置分页数据总个数，用于处理分页
            pageNum: 1,  // 默认页码，选则显示第几页数据
            pageSize: 10,  // 每页显示条目个数
        }
    },

    async created() {
        await this.takeAccomplishPageData()
    },

    methods: {

        /** 分页功能按钮 **/
        handleSizeChange(val) {
            this.pageSize = val
            this.takeAccomplishPageData()
        },
        handleCyrRentChange(val) {
            this.pageNum = val
            this.takeAccomplishPageData()
        },

        /** 查询-获取基础分页查询数据 **/
        async takeAccomplishPageData() {
            const tableData = []
            const params = {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
                type: this.type,
                videoId: this.videoId
            };
            const date = this.$http.getAccomplishTakeListData(params)
            await date.then(res => {
                const returnData = res.data  // 后台返回的主要数

                this.total = returnData.total
                const records = returnData.records
                records.forEach(function (value) {
                    tableData.push(value)
                });
                this.tableData = tableData
            })
        },


        /** 查看已办任务的流程记录 **/
        handleFlowRecord(row){
            const processInstanceId = row.procInsId;
            const deployId = row.deployId;
            const taskId = row.taskId;

            this.$router.push({
                path: '/flow/my/process/details',
                query: {
                    processInstanceId: processInstanceId,
                    deployId: deployId,
                    taskId: taskId,
                }
            })
        },

    }
}
</script>

<style scoped>

</style>