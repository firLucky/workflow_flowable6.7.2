<!--
    流程申请
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
                    <fromInfo ref="fromFunction" :form-data=this.fromData :disabledKey=true></fromInfo>
                    <el-row class="row-bu">
                        <el-form-item>
                            <el-button type="primary" size="mini" @click="submitForm()"><i
                                class="iconfont icon-a-10queding"></i>提交申请
                            </el-button>
                            <el-button type="info" size="mini" @click="resetForm()">
                                <i class="iconfont icon-a-11quxiao"></i>重置
                            </el-button>
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
                    <my-process-viewer key="designer" v-model="bpmnXML" v-bind="bpmnControlForm"/>
                </el-col>
            </el-card>

        </div>

    </div>
</template>

<script>
import fromInfo from "@/views/flow/form/fromInfo"
import myProcessViewer from '@/views/bmp/ProcessViewer'

export default {
    name: "processDeal",

    components: {
        fromInfo,
        myProcessViewer,
    },

    data() {
        return {
            currentTarget: "",
            // 流程定义id
            processDefinitionId : "",
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
        }
    },

    async created() {
        // 根据接受的数值不同将显示不同的内容
        const currentTarget = this.$route.query;
        this.processDefinitionId = currentTarget.processDefinitionId;
        this.deployId = currentTarget.deployId;

        this.getDetail();
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
        },

        /** 返回页面 */
        goBack() {
            // 关闭当前标签页并返回上个页面
            this.$store.dispatch("tagsView/delView", this.$route);
            this.$router.go(-1)
        },


        /** 提交流程申请 **/
        submitForm() {
            // 获取子组件表单数据
            let formValue = this.$refs.fromFunction.getFormValue();

            formValue = encodeURIComponent(JSON.stringify(formValue));
            const params = {
                processDefinitionId: this.processDefinitionId,
                variables: formValue,
            }

            this.$http.submitFormProcessDeal(params).then(res => {
                let code = res.code
                let msg = res.msg

                if (code === 200) {
                    this.goBack()
                    this.success({message: msg, duration: 1.5, description: ''})
                } else {
                    this.error({message: msg, duration: 1.5, description: ''})
                }
            })
        },


        /** 重置流程申请信息 **/
        resetForm() {
            this.$refs.fromFunction.cleanFormValue();
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