<template>

    <div class="back-container">
        节点审批人设置

        <!-- 表单设置 -->
        <el-table :data="tableData" border>
            <el-table-column label="流程编号" align="center" prop="id" :show-overflow-tooltip="true"/>
            <el-table-column label="流程名称" align="center" prop="name" :show-overflow-tooltip="true"/>
            <el-table-column label="流程类别" align="center" prop="category" width="100px"/>
            <el-table-column label="部署ID" align="center" prop="deploymentId" width="180"/>
            <el-table-column label="部署时间" align="center" prop="deploymentTime" width="180"/>

            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">

                <template slot-scope="scope">
                    <el-button type="primary" size="mini" style="margin: 0 5px 0 0 ;"
                               @click="keyPersonFlowKeyParticulars(scope.row)">
                        <i class="el-icon-tickets"></i>
                        节点详情
                    </el-button>

                </template>
            </el-table-column>
        </el-table>
        <el-pagination @size-change="btSizeKeyPerson" @current-change='btPageKeyPerson' :current-page="pageNum"
                       :page-sizes="[10, 20, 50, 100]" :page-size="pageSize"
                       layout="total, sizes, prev, pager, next, jumper"
                       :total="total"></el-pagination>


    </div>
</template>

<script>

export default {
    name: "keyPerson",

    components: {
    },

    data() {
        return {
            // 流程列表数据
            tableData: [],

            // 分页数据
            total: 10,  // 设置分页数据总个数，用于处理分页
            pageNum: 1,  // 默认页码，选则显示第几页数据
            pageSize: 10,  // 每页显示条目个数

        }
    },


    async created() {
        await this.keyPersonPageData();

    },

    methods: {

        /** 分页功能按钮-切换页 **/
        btPageKeyPerson(val) {
            this.pageNum = val
            this.keyPersonPageData()
        },


        /** 分页功能按钮-设置每页个数 **/
        btSizeKeyPerson(val){
            this.pageSize = val
            this.keyPersonPageData()
        },


        /** 查询-获取基础分页查询数据 **/
        async keyPersonPageData() {

            const params = {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
            };
            const date = this.$http.definitionPageData(params)
            await date.then(res => {
                const returnData = res.data
                this.total = returnData.total
                this.tableData = returnData.records
            })
        },


        /** 节点详情-跳转审批人设置界面 */
        keyPersonFlowKeyParticulars(row) {
            const processDefinitionId = row.id;
            const deployId = row.deploymentId;
            this.$router.push({
                path: '/take/key/person/select',
                query: {
                    processDefinitionId: processDefinitionId,
                    deployId: deployId
                }
            })

        },
    },
}

</script>

<style scoped>

</style>