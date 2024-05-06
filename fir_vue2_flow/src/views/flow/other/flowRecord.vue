<template>

    <el-col :span="16" :offset="4">
        <div class="block">
            <el-timeline>
                <el-timeline-item v-for="(item,index ) in flowRecordList" :key="index"
                                  :icon="setIcon(item.finishTime)" :color="setColor(item.finishTime)">
                    <p style="font-weight: 700">{{ item.taskName }}</p>
                    <el-card :body-style="{ padding: '10px' }">
                        <el-descriptions class="margin-top" :column="1" size="small" border>
                            <el-descriptions-item v-if="item.assigneeName" label-class-name="my-label">
                                <template slot="label">
                                    <i class="el-icon-user"></i>
                                    实际办理
                                </template>
                                {{ item.assigneeName }}
                                <el-tag type="info">{{ item.deptName }}</el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item v-if="item.candidate" label-class-name="my-label">
                                <template slot="label">
                                    <i class="el-icon-user"></i>
                                    候选办理
                                </template>
                                {{ item.candidate }}
                            </el-descriptions-item>
                            <el-descriptions-item label-class-name="my-label">
                                <template slot="label">
                                    <i class="el-icon-date"></i>
                                    接收时间
                                </template>
                                {{ item.createTime }}
                            </el-descriptions-item>
                            <el-descriptions-item v-if="item.finishTime" label-class-name="my-label">
                                <template slot="label">
                                    <i class="el-icon-date"></i>
                                    处理时间
                                </template>
                                {{ item.finishTime }}
                            </el-descriptions-item>
                            <el-descriptions-item v-if="item.duration" label-class-name="my-label">
                                <template slot="label">
                                    <i class="el-icon-time"></i>
                                    耗时
                                </template>
                                {{ item.duration }}
                            </el-descriptions-item>
                            <el-descriptions-item v-if="item.comment" label-class-name="my-label">
                                <template slot="label">
                                    <i class="el-icon-tickets"></i>
                                    处理意见
                                </template>
                                {{ item.comment.comment }}
                            </el-descriptions-item>
                            <el-descriptions-item v-if="item.comment" label-class-name="my-label">
                                <template slot="label">
                                    <i class="el-icon-tickets"></i>
                                    审批结果
                                </template>
                                <el-button type="success" plain v-if="item.comment.type === '1'">
                                    已通过
                                </el-button>
                                <el-button type="info" plain v-if="item.comment.type === '2'">
                                    已退回
                                </el-button>
                                <el-button type="danger" plain v-if="item.comment.type === '3'">
                                    已驳回
                                </el-button>
                                <el-button type="danger" plain v-if="item.comment.type === '7'">
                                    用户取消流程
                                </el-button>
                                <el-button type="danger" plain v-if="item.comment.type === '8'">
                                    审批人驳回流程
                                </el-button>
                            </el-descriptions-item>
                        </el-descriptions>
                    </el-card>
                </el-timeline-item>
            </el-timeline>
        </div>
    </el-col>
</template>

<script>
export default {
    name: "flowRecord",
    props: ["processInstanceId"],

    data() {
        return {
            // 流程流转数据
            flowRecordList: [],
        }
    },

    created() {
        this.flowRecordListData();
    },

    methods: {

        flowRecordListData() {
            /** 获取审批记录 **/

            const data = {
                procInsId: this.processInstanceId
            }
            // 发送请求，获取xml
            this.$http.getFlowRecordListData(data).then(res => {

                let data = res.data;
                this.flowRecordList = data.flowList;

            }).finally(
            )
        },

        /** 任务完成与未完成状态图标 **/
        setIcon(val) {
            if (val) {
                return "el-icon-check";
            } else {
                return "el-icon-time";
            }
        },

        /** 任务完成与未完成状态图标-颜色(绿色完成,白色完成) **/
        setColor(val) {
            if (val) {
                return "#2bc418";
            } else {
                return "#b3bdbb";
            }
        },
    }
}
</script>

<style scoped>

</style>