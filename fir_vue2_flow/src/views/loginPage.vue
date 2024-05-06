<template>
    <div v-loading="loading" element-loading-text="登录中..." element-loading-spinner="el-icon-loading"
         element-loading-background="rgba(0, 0, 0, 0.6)" class="login-container">


        <div class="login-page">
            <div class="login-page-head"></div>
            <div class="login-page-login">
                <div class="login-page-login-headline">
                    <h2 class="login-page-login-headline-h2">工作流</h2>
                    <h3 class="login-page-login-headline-h3">FIR</h3>
                </div>
                <el-main class="inputField">
                    <div class="inputFieldHeadline">
                        <div class="inputFieldHeadline-text">登录</div>
                    </div>
                    <el-form
                        ref="formRef"
                        :model="numberValidateForm"
                        class="demo-ruleForm"
                        label-width="100px"
                        status-icon
                        @close="closeApply"
                    >
                        <el-form-item
                            label="账号"
                            prop="name"
                        >

                            <el-input
                                class="nameInput"
                                v-model="numberValidateForm.username"
                                placeholder="请输入账号"
                                type="text"
                                autocomplete="off"
                            />
                        </el-form-item>
                        <el-form-item
                            label="密码"
                            prop="phoneNum"
                        >
                            <el-input
                                class="phoneNumInput"
                                v-model="numberValidateForm.password"
                                type="password"
                                placeholder="请输入密码"
                                show-password
                                autocomplete="off"
                            />
                        </el-form-item>

                        <el-form-item>
                            <el-button type="primary" @click="confirmEvent(numberValidateForm)">登录
                            </el-button>
                        </el-form-item>
                    </el-form>

                </el-main>
            </div>
        </div>
    </div>
</template>

<script>

export default {
    name: "loginPage",
    components: {},
    data() {
        return {
            numberValidateForm: {
                username: "",
                password: "",
                brief: "",
            },
            rules: [
                {required: true, message: '必填项'},
            ],

            // 登录中加载框
            loading: false,
        };
    },

    methods: {

        /** 登录提示框 **/
        async confirmEvent(item) {
            this.loading = true;
            // let loginMsg = "登录失败：系统异常"
            let loginKey = false;
            try {
                await this.$http.login(item).then(res => {
                    const data = res.data;
                    const code = res.code;
                    const msg = res.msg;
                    const result = data;
                    loginKey = true
                    if (code === 200) {
                        this.$message.success(msg)
                        // 存储登录结果token至localStorage
                        const token = result.token;
                        localStorage.setItem("token", token)
                        // 登录成功切换界面
                        this.$router.push({path: '/home'});
                    } else {
                        this.$message.error(msg)
                    }

                })
            }finally {
                this.loading = false;
                // 登录判断
                if(!loginKey){
                    this.$message.error("登录失败：系统异常")
                }
            }
        },

        closeApply() {

        }
    },

}
</script>

<style scoped>


.login-container {
    min-height: 100%;
    width: 100%;
    height: 100%;
    overflow: hidden;
    background-size: 100% 100%;
}


.login-page {
    width: 100%;
    height: 100%;
    padding: 0; /*清除内边距*/
    margin: 0; /*清除外边距*/
    background: url("../assets/image/login.jpg") no-repeat;
    background-size: cover; /*设置背景图片大小*/
}

.login-page-head {
    width: 100%;
    height: 200px;
}

.login-page-login {
    width: 100%;
    height: 44.5%;
    min-height: 413px;
    background-color: rgba(255, 255, 255, 0.38);
}

.login-page-login-headline {
    width: 65%;
    height: 25%;
    float: left;
}

.login-page-login-headline-h2 {
    font-size: 40px;
    margin-right: 30%; /*设置元素的右外边距*/
    margin-left: 30%; /*设置元素的右外边距*/
    float: right;
}

.login-page-login-headline-h3 {
    font-size: 20px;
    margin-right: 10%; /*设置元素的右外边距*/
    float: right;
    font-style: italic; /*设置斜体*/
}

/*一下为申请单设置*/
.inputField {
    /*position:absolute;  !*设置置于底部*!*/
    bottom: 1%; /*与position 配合使用 设置底部位置*/
    /*right: 1%;  !*设置右侧位置*!*/
    width: 19%;
    height: 90%;
    min-width: 364px;
    background-color: #ffffff;
    float: right;
    margin-top: 1%; /*设置元素的上外边距*/
    margin-right: 10%; /*设置元素的右外边距*/
    margin-bottom: 1%; /*设置元素的下外边距*/
    /*margin-left: auto;  !*设置元素的左外边距*!*/
}

.inputFieldHeadline {
    width: 100%;
    height: 70px;
}

.inputFieldHeadline-text {
    text-align: center; /*设置水平居中*/
    font-size: 15px; /*设置字体尺寸*/
    color: rgba(45, 42, 42, 0.52);
}

.nameInput {
    width: 150px;
}

.phoneNumInput {
    width: 150px;
}

</style>
