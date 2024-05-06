<template>

    <div class="back-container">
        待办任务
        <!-- 表单设置 -->
        <el-table  :data="tableData" border>
            <el-table-column label="任务编号" align="center" prop="taskId" :show-overflow-tooltip="true"/>
            <el-table-column label="流程名称" align="center" prop="procDefName"/>
            <el-table-column label="任务节点" align="center" prop="taskName"/>
            <el-table-column label="流程版本" align="center">
                <template #default="scope">
                    <el-tag>v{{ scope.row.procDefVersion }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="流程发起人" align="center">
                <template #default="scope">
                    <label>{{ scope.row.startUserName }}
                        <el-tag type="info">{{ scope.row.startDeptName }}</el-tag>
                    </label>
                </template>
            </el-table-column>
            <el-table-column label="接收时间" align="center" prop="createTime" width="180"/>
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-button  type="primary" size="mini" style="margin: 0px 5px 0 0 ;"
                                @click="jumpDealWithTake(scope.row)">
                        <i class="el-icon-edit"></i>
                        处理</el-button>
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
    name: "takeDealWith",

    components: {
    },

    data() {
        return {

            path: '/about',

            // 我的流程列表
            tableData: [],

            // 分页数据
            total: 10,  // 设置分页数据总个数，用于处理分页
            pageNum: 1,  // 默认页码，选则显示第几页数据
            pageSize: 10,  // 每页显示条目个数
        }
    },

    created() {
        this.animalPageData()
    },

    methods:{

        /** 分页功能按钮 **/
        handleSizeChange(val) {
            this.pageSize = val
            this.animalPageData()
        },
        handleCyrRentChange(val) {
            this.pageNum = val
            this.animalPageData()
        },

        /** 查询-获取基础分页查询数据 **/
        async animalPageData() {
            const tableData = []
            const params = {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
            };
            const date = this.$http.getDealWithTakeListData(params)
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


        /** 处理待办任务按钮，跳转审核代办任务界面 */
        jumpDealWithTake(row){
            const procInsId = row.procInsId;
            const deployId = row.deployId;
            const taskId = row.taskId;

            this.$router.push({ path: '/flow/my/process/deal', query: { processInstanceId: procInsId, deployId: deployId, taskId: taskId}})

        },
    }
}
</script>

<style scoped>

</style>