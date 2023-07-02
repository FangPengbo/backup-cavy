<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">cavy</div>
            <el-form :model="param" :rules="rules" ref="login" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="param.account" placeholder="account">
                        <template #prepend>
                            <el-button icon="el-icon-user"></el-button>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" placeholder="password" v-model="param.password"
                        @keyup.enter="submitForm()">
                        <template #prepend>
                            <el-button icon="el-icon-lock" @click="sendCode()"></el-button>
                        </template>
                    </el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="submitForm()">登录</el-button>
                </div>
<!--                <p class="login-tips">Tips : 用户名和密码随便填。</p>-->
            </el-form>
        </div>
    </div>
</template>

<script>
import { ref, reactive } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import {Login, CheckToken, SendCode} from "../api/index";


export default {
    setup() {
      const router = useRouter();
      const token = localStorage.getItem("token");
      CheckToken(token).then((res) => {
        console.log("checkToken: " + res)
        if (res.Code === 0){
          router.push("/");
        }
      });

      let count = 0;

      const param = reactive({
          account: "",
          password: "",
        });
        const rules = {
          account: [
                {
                    required: true,
                    message: "请输入用户名",
                    trigger: "blur",
                },
            ],
            password: [
                { required: true, message: "请输入密码", trigger: "blur" },
            ],
        };
        const login = ref(null);

        const sendCode = () =>{
          count++
          if (count >= 5){
            count = -100
            //禁止点击
            ElMessage.success("验证码发送中...");
            //发送验证码
            SendCode().then((res) => {
              if (res.Code === 0){
                ElMessage.success("验证码发送成功");
              }else {
                ElMessage.error("验证码发送失败");
                return false;
              }
            });
          }
          console.log("点击次数：" + count)
        }

        const submitForm = () => {
          var data = {
            "account":param.account,
            "code":param.password
          }
          // 获取登录结果
          const getData = () => {
            Login(data).then((res) => {
              if (res.Code === 0){
                ElMessage.success("登录成功");
                localStorage.setItem("token", res.Data);
                localStorage.setItem("ms_username", param.account);
                router.push("/");
              }else {
                ElMessage.error("登录失败");
                return false;
              }
            });
          };
          getData();
        };

        const store = useStore();
        store.commit("clearTags");

        return {
            param,
            rules,
            login,
            submitForm,
            sendCode,
        };
    },
};
</script>

<style scoped>
.login-wrap {
    position: relative;
    width: 100%;
    height: 100%;
    background-image: url(../assets/img/login-bg.jpg);
    background-size: 100%;
}
.ms-title {
    width: 100%;
    line-height: 50px;
    text-align: center;
    font-size: 26px;
    color: #fff;
    border-bottom: 1px solid #ddd;
}
.ms-login {
    position: absolute;
    left: 50%;
    top: 50%;
    width: 350px;
    margin: -190px 0 0 -175px;
    border-radius: 5px;
    background: rgba(255, 255, 255, 0.3);
    overflow: hidden;
}
.ms-content {
    padding: 30px 30px;
}
.login-btn {
    text-align: center;
}
.login-btn button {
    width: 100%;
    height: 36px;
    margin-bottom: 10px;
}
.login-tips {
    font-size: 12px;
    line-height: 30px;
    color: #fff;
}
</style>