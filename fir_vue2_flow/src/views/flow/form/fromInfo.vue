<template>
    <div class="from">
        <el-form>
            <el-row>
                <el-col :span="18">
                    <el-form-item label="申请人" label-width="120px" prop="deviceId">
                        <el-input :disabled="!disabledKey" v-model="fromData.username" placeholder="请输入申请人"
                            style="width: 125.5%;" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="22">
                    <el-form-item label="申请原因" label-width="120px" prop="reason">
                        <el-input type="textarea" :autosize="{minRows:4,maxRows:8}" :disabled="!disabledKey"
                            v-model="fromData.reason" placeholder="请输入申请原因" style="width:100%">
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row>
                <el-col :span="22">
                    <el-form-item label="时间" label-width="120px" prop="reason">
                        <el-date-picker v-model="fromData.date" type="daterange" range-separator="至" :disabled="!disabledKey"
                            value-format="yyyy-MM-dd" start-placeholder="开始时间" end-placeholder="结束时间">
                        </el-date-picker>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
    </div>
</template>

<script>
    export default {
        name: "fromInfo",

        props: ["disabledKey"],


        data() {
            return {
                // 流程申请信息对象
                fromData: {
                    username: "",
                    reason: "",
                    data: "",
                },
            }
        },

        methods: {

            // 赋值当前表单信息
            setFormValue(data) {
                this.fromData = data;
            },

            // 返回当前表单信息
            getFormValue() {
                return this.fromData;
            },

            // 清空表单信息
            cleanFormValue() {
                this.fromData = {};
            }

        },
    }
</script>

<style scoped>
    .from {
        margin: 0 auto;
        width: 80%;
        height: 40%;
    }
</style>