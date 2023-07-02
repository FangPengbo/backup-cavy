<template>
    <div>
        <el-row>
            <el-col >
                <el-row :gutter="20" class="mgb20">
                    <el-col :span="8">
                        <el-card shadow="hover" :body-style="{ padding: '0px' }">
                            <div class="grid-content grid-con-1">
                                <i class="el-icon-coin grid-con-icon"></i>
                                <div class="grid-cont-right">
                                    <div class="grid-num">{{ dataSourceSize }}</div>
                                    <div>数据源个数</div>
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                    <el-col :span="8">
                        <el-card shadow="hover" :body-style="{ padding: '0px' }">
                            <div class="grid-content grid-con-2">
                                <i class="el-icon-files grid-con-icon"></i>
                                <div class="grid-cont-right">
                                    <div class="grid-num">{{ runningJobSize }}</div>
                                    <div>运行中的备份任务</div>
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                    <el-col :span="8">
                        <el-card shadow="hover" :body-style="{ padding: '0px' }">
                            <div class="grid-content grid-con-3">
                                <i class="el-icon-cloudy grid-con-icon"></i>
                                <div class="grid-cont-right">
                                    <div class="grid-num">{{ countFileSize }}</div>
                                    <div>总备份文件大小</div>
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                </el-row>
            </el-col>
        </el-row>
        <el-row :gutter="20">
            <el-col :span="12">
                <el-card shadow="hover">
                    <schart ref="bar" class="schart" canvasId="bar" :options="options"></schart>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card shadow="hover">
                    <schart ref="line" class="schart" canvasId="line" :options="options2"></schart>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import Schart from "vue-schart";
import {reactive, ref} from "vue";
import {Dashboard} from "../../api";
import {ElMessage} from "element-plus";

export default {
  name: "dashboard",
  components: {Schart},
  setup() {
    const dataSourceSize = ref(0)
    const runningJobSize = ref(0)
    const countFileSize = ref("")
    const dateList = ref([])
    const optionsSuccess = ref([])
    const optionsFail = ref([])
    const options2Success = ref([])
    const options2Fail = ref([])

    const options =  ref({
      type: "bar",
      title: {
        text: "最近一周备份状况",
      },
      xRorate: 25,
      labels: dateList.value,
      datasets: [
        {
          label: "成功",
          fillColor: "rgba(47,94,50,0.7)",
          data: optionsSuccess.value,
        },
        {
          label: "失败",
          fillColor: "rgba(176,31,43,0.7)",
          data: optionsFail.value,
        },
      ],
    });

    const options2 = ref({
      type: "line",
      title: {
        text: "最近一周备份成功率(%)",
      },
      labels: dateList.value,
      datasets: [
        {
          label: "成功",
          fillColor: "rgba(47,94,50,0.7)",
          data: options2Success.value,
        },
        {
          label: "失败",
          fillColor: "rgba(176,31,43,0.7)",
          data: options2Fail.value,
        }
      ],
    });

    const getData = () => {
      Dashboard().then((res) => {
        if (res.code === '0') {
          dataSourceSize.value = res.result.dataSourceSize
          runningJobSize.value = res.result.runningJobSize
          countFileSize.value = byteToStr(res.result.countFileSize)

          options.value.labels = res.result.dateList
          options.value.datasets[0].data = res.result.optionsSuccess
          options.value.datasets[1].data = res.result.optionsFail
          options2.value.labels = res.result.dateList
          options2.value.datasets[0].data = res.result.options2Success
          options2.value.datasets[1].data = res.result.options2Fail
        } else {
          ElMessage.error("获取Dashboard数据失败：" + res.message);
        }
      });
    };

    getData();

    const byteToStr = (size) =>{
      if (!size)  return "";
      var num = 1024.00; //byte
      if (size < num)
        return size + "B";
      if (size < Math.pow(num, 2))
        return (size / num).toFixed(2) + "KB"; //kb
      if (size < Math.pow(num, 3))
        return (size / Math.pow(num, 2)).toFixed(2) + "MB"; //M
      if (size < Math.pow(num, 4))
        return (size / Math.pow(num, 3)).toFixed(2) + "G"; //G
      return (size / Math.pow(num, 4)).toFixed(2) + "T"; //T
    }

    return {
      countFileSize,
      runningJobSize,
      dataSourceSize,
      options,
      options2,
      getData,
    };
  },
};
</script>

<style scoped>

.el-row {
    margin-bottom: 20px;
}

.grid-content {
    display: flex;
    align-items: center;
    height: 100px;
}

.grid-cont-right {
    flex: 1;
    text-align: center;
    font-size: 14px;
    color: #999;
}

.grid-num {
    font-size: 30px;
    font-weight: bold;
}

.grid-con-icon {
    font-size: 50px;
    width: 100px;
    height: 100px;
    text-align: center;
    line-height: 100px;
    color: #fff;
}

.grid-con-1 .grid-con-icon {
    background: rgb(45, 140, 240);
}

.grid-con-1 .grid-num {
    color: rgb(45, 140, 240);
}

.grid-con-2 .grid-con-icon {
    background: rgb(100, 213, 114);
}

.grid-con-2 .grid-num {
    color: rgb(45, 140, 240);
}

.grid-con-3 .grid-con-icon {
    background: #8CABD9;
}

.grid-con-3 .grid-num {
  color: rgb(45, 140, 240);
}

.user-info {
    display: flex;
    align-items: center;
    padding-bottom: 20px;
    border-bottom: 2px solid #ccc;
    margin-bottom: 20px;
}

.user-avator {
    width: 120px;
    height: 120px;
    border-radius: 50%;
}

.user-info-cont {
    padding-left: 50px;
    flex: 1;
    font-size: 14px;
    color: #999;
}

.user-info-cont div:first-child {
    font-size: 30px;
    color: #222;
}

.user-info-list {
    font-size: 14px;
    color: #999;
    line-height: 25px;
}

.user-info-list span {
    margin-left: 70px;
}

.mgb20 {
    margin-bottom: 20px;
}

.todo-item {
    font-size: 14px;
}

.todo-item-del {
    text-decoration: line-through;
    color: #999;
}

.schart {
    width: 100%;
    height: 500px;
}
</style>
