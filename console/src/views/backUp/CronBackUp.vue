<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <i class="el-icon-lx-cascades"></i>周期性备份任务
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="container">
      <div class="handle-box">
        <el-select v-model="query.dataSourceId" placeholder="数据源" filterable clearable class="handle-input mr10">
          <el-option v-for="item in dataSources" :key="item.id" :label="item.sourceName" :value="item.id"></el-option>
        </el-select>
        <el-input v-model="query.jobName" placeholder="任务名称" class="handle-input mr10"></el-input>
        <el-select v-model="query.jobState" placeholder="最近一次任务状态" filterable clearable  class="handle-input mr10">
          <el-option key="NORMAL" label="正常" value="NORMAL"></el-option>
          <el-option key="RUNNING" label="运行中" value="RUNNING"></el-option>
          <el-option key="SUCCESS" label="成功" value="SUCCESS"></el-option>
          <el-option key="FAIL" label="失败" value="FAIL"></el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增</el-button>
      </div>
      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="id" label="ID" align="center"></el-table-column>
        <el-table-column prop="dataSourceName" label="数据源"></el-table-column>
        <el-table-column prop="jobName" label="任务名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="jobDescribe" label="描述" width="150" show-overflow-tooltip></el-table-column>
        <el-table-column
            prop="jobState"
            label="最近一次任务状态"
            :filters="[
              { text: '正常', value: 'NORMAL' },
              { text: '运行中', value: 'RUNNING' },
              { text: '成功', value: 'SUCCESS' },
              { text: '失败', value: 'FAIL' },
              ]"
            :filter-method="filterState"
            filter-placement="bottom-end"
        >
          <template #default="scope">
            <el-tag :type="getStateTag(scope.row.jobState)">
              {{ scope.row.jobState }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
            prop="jobEnable"
            label="任务运行状态"
            :filters="[
              { text: '关闭', value: 'OFF' },
              { text: '开启', value: 'ON' },
              ]"
            :filter-method="filterEnable"
            filter-placement="bottom-end"
        >
          <template #default="scope">
            <el-tag :type="getStateTag(scope.row.jobEnable)">
              {{ scope.row.jobEnable }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cronExpression" label="cron表达式"></el-table-column>
        <el-table-column prop="executeTime" label="最近一次执行时间"></el-table-column>
        <el-table-column prop="lastTime" label="上次执行时间"></el-table-column>
        <el-table-column prop="nextTime" label="下次执行时间"></el-table-column>
        <el-table-column label="操作" align="center" min-width="120">
          <template #default="scope">
            <el-button-group class="ml-4">
            <el-button icon="el-icon-view" size="small" @click="handleView(scope.$index, scope.row)"></el-button>
            <el-button icon="el-icon-open" size="small" @click="handleEnable(scope.$index, scope.row)"></el-button>
            <el-button icon="el-icon-edit" size="small" @click="handleEdit(scope.$index, scope.row)"></el-button>
            <el-button icon="el-icon-delete" class="red" size="small" @click="handleDelete(scope.$index, scope.row)"></el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination background layout="total, prev, pager, next" :current-page="query.page"
                       :page-size="query.size" :total="pageTotal"
                       @current-change="handlePageChange"></el-pagination>
      </div>
    </div>

    <!-- 新增job任务 -->
    <el-dialog title="新增周期性性任务" v-model="addVisible" width="50%">
      <el-form label-width="100px">
        <el-form-item label="任务名称" :required="true" >
          <el-input v-model="addForm.jobName"></el-input>
        </el-form-item>
        <el-form-item label="数据源" :required="true">
          <el-select v-model="addForm.dataSourceId" placeholder="请选择" filterable @change="selectDataSource">
            <el-option v-for="item in dataSources" :key="item.id" :label="item.sourceName" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数据库">
          <el-transfer v-model="dataBase" :data="allDataBase" :titles="['所有数据库', '要备份的数据库']" filterable/>
        </el-form-item>
        <el-form-item label="cron表达式">
          <el-input v-model="cronExpression">
            <template #suffix>
              <el-tooltip placement="right">
                <template #content><span v-for="el in lastRunTimeData">{{el}}<br></span></template>
                <el-button type="primary" @mouseover="lastRunTime" style="position: relative;left: 5px">
                  <span>最近运行时间</span>
                </el-button>
              </el-tooltip>
            </template>
          </el-input>

        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="addForm.jobDescribe" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
                <span class="dialog-footer">
                    <el-button @click="addVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveAdd">确 定</el-button>
                </span>
      </template>
    </el-dialog>


    <!-- 编辑job任务 -->
    <el-dialog title="编辑任务" v-model="editVisible" width="50%">
      <el-form label-width="100px">
        <el-form-item label="任务名称" :required="true" >
          <el-input v-model="form.jobName"></el-input>
        </el-form-item>
        <el-form-item label="数据源" :required="true">
          <el-select v-model="form.dataSourceId" placeholder="请选择" filterable @change="selectDataSource">
            <el-option v-for="item in dataSources" :key="item.id" :label="item.sourceName" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数据库">
          <el-transfer v-model="dataBase" :data="allDataBase" :titles="['所有数据库', '要备份的数据库']" filterable/>
        </el-form-item>
        <el-form-item label="cron表达式">
          <el-input v-model="cronExpression">
            <template #suffix>
              <el-tooltip placement="right">
                <template #content><span v-for="el in lastRunTimeData">{{el}}<br></span></template>
                <el-button type="primary" @mouseover="lastRunTime" style="position: relative;left: 5px">
                  <span>最近运行时间</span>
                </el-button>
              </el-tooltip>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.jobDescribe" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
                <span class="dialog-footer">
                    <el-button @click="editVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveEdit">确 定</el-button>
                </span>
      </template>
    </el-dialog>

    <!-- job执行记录   -->
    <Job :viewVisible="viewVisible" :job-record-data="jobRecordData" :key="timer"></Job>

  </div>
</template>

<script>
import Job from '../../components/Job.vue'
import {ref, reactive} from "vue";
import {ElMessage, ElMessageBox,ElDrawer} from "element-plus";
import {
  AllDataBaseByDataSource, AllDataSource,
  BackUpJobRecordList,
  BackUpList,
  DeleteBackUpJob,
  EnableBackUpJob, InsertBackUpJob,
  LastRunTime, UpdateBackUpJob,
} from "../../api/index";

export default {
  name: "backUpTable",
  components:{Job},
  setup() {
    const timer = ref("")
    const cronExpression = ref()
    const defaultDataBase = ref(["information_schema","mysql","performance_schema","sys"])
    const lastRunTimeData = ref([])
    const dataSources = ref([])
    const dataBase = ref([])
    const allDataBase = ref([])
    const backUpDataBase = ref([])
    const visible = ref(false)
    const deleteId = ref(0)
    const editVisible = ref(false);
    const viewVisible = ref(false);
    const tableData = ref([]);
    const jobRecordData = ref([]);
    const pageTotal = ref(0);
    const addVisible = ref(false);
    const query = reactive({
      jobName:"",
      jobState:"",
      dataSourceId:"",
      page: 1,
      size: 10,
      jobType: 1,
    });
    const addForm = reactive({
      jobName: "",
      jobDescribe: "",
      jobType: 1,
      dataSourceId: "",
      cronExpression: "",
      jobPolicy:"",
    });
    const form = reactive({
      id:0,
      jobName: "",
      jobDescribe: "",
      jobType: 1,
      dataSourceId: "",
      cronExpression: "",
      jobPolicy:"",
    });

    const saveEdit = () => {
      form.cronExpression = cronExpression.value
      let policy = {}
      let tempBackUpDataBase = []
      let tempExcludeDataBase = []
      for (let i = 1; i <= backUpDataBase.value.length; i++) {
        let el = backUpDataBase.value[i - 1]
        if (dataBase.value.includes(i)){
          tempBackUpDataBase.push(el)
        }else {
          tempExcludeDataBase.push(el)
        }
      }
      policy["backUpDataBase"] = tempBackUpDataBase
      policy["excludeDataBase"] = tempExcludeDataBase
      form.jobPolicy = JSON.stringify(policy)
      UpdateBackUpJob(form).then((res) => {
        if (res.code === '0') {
          ElMessage.success("更新备份任务成功");
          getData()
        } else {
          ElMessage.error("更新备份任务失败" + res.message);
        }
      })
      editVisible.value = false;
    }

    const selectDataSource = (id) => {
      AllDataBaseByDataSource(id).then((res) => {
        if (res.code === '0') {
          backUpDataBase.value = res.result
          for (let i = 1; i <= backUpDataBase.value.length; i++) {
            let el = backUpDataBase.value[i - 1]
            allDataBase.value.push({
              key: i,
              label: el,
              disabled: !!defaultDataBase.value.includes(el)
            })
          }
        } else {
          ElMessage.error("获取所有数据源下所有数据库失败:" + res.message);
        }
      });
    }

    const allDataSource = () =>{
      AllDataSource().then((res) =>{
        if (res.code === '0') {
          dataSources.value = res.result
        } else {
          console.log("获取所有数据源列表失败")
          ElMessage.error("获取所有数据源列表失败:" + res.message);
        }
      });
    }

    // 获取表格数据
    const getData = () => {
      allDataSource()
      BackUpList(query).then((res) => {
        tableData.value = res.result.records;
        pageTotal.value = res.result.total || 50;
      });
    };
    getData();

    const filterEnable = (value, row) => {
      return row.jobEnable === value
    }

    const filterType = (value, row) => {
      return row.jobType === value
    }
    const filterState = (value, row) => {
      return row.jobState === value
    }

    const filterJobResult = (value, row) => {
      return row.jobResult === value
    }

    const filterJobRecordResult = (value, row) => {
      return row.result === value
    }

    //新增操作
    const handleAdd = () => {
      addVisible.value = true;
      allDataSource();
    }
    // 查询操作
    const handleSearch = () => {
      query.page_num = 1;
      getData();
    };
    // 分页导航
    const handlePageChange = (val) => {
      query.page = val;
      getData();
    };
    // 删除操作
    const handleDelete = (index, row) => {
      idx = index;
      ElMessageBox.confirm(
          '是否删除这个任务',
          {
            confirmButtonText: 'OK',
            cancelButtonText: 'Cancel',
            type: 'warning',
          }
      ).then(() => {
        DeleteBackUpJob(row.id).then((res) => {
          if (res.code === '0') {
            ElMessage.success("删除成功");
            getData()
          } else {
            ElMessage.error("删除失败" + res.message);
          }
        })
      }).catch(() => {
      })
    };

    let idx = -1;
    const handleEdit = (index, row) => {
      allDataBase.value = []
      dataBase.value = []
      console.log(allDataBase.value)
      console.log(dataBase.value)

      idx = index;
      allDataSource();

      Object.keys(form).forEach((item) => {
        form[item] = row[item];
      });
      cronExpression.value = row.cronExpression

      let policy = JSON.parse(form.jobPolicy)
      let tempBackUpDataBase = []

      tempBackUpDataBase = policy["backUpDataBase"]

      AllDataBaseByDataSource(row.dataSourceId).then((res) => {
        if (res.code === '0') {
          backUpDataBase.value = res.result
          for (let i = 1; i <= backUpDataBase.value.length; i++) {
            let el = backUpDataBase.value[i - 1]
            allDataBase.value.push({
              key: i,
              label: el,
              disabled: !!defaultDataBase.value.includes(el)
            })
            if (tempBackUpDataBase.includes(el)){
              dataBase.value.push(i)
            }
          }
        } else {
          ElMessage.error("获取所有数据源下所有数据库失败:" + res.message);
        }
      });
      editVisible.value = true;
    };

    const handleView = (index, row) => {
      idx = index;
      BackUpJobRecordList(row.id).then((res) => {
        jobRecordData.value = res.result;
      })
      timer.value = new Date().getTime()
      viewVisible.value = true;
    };


    const handleEnable = (index, row) => {
      idx = index;
      ElMessageBox.confirm(
          '是否' + (row.jobEnable === 'OFF' ? '开启' : '关闭') + '这个任务',
          {
            confirmButtonText: 'OK',
            cancelButtonText: 'Cancel',
            type: 'warning',
          }
      ).then(() => {
            EnableBackUpJob(row.id).then((res) => {
              if (res.code === '0') {
                ElMessage.success("操作成功");
                getData()
              } else {
                ElMessage.error("操作失败" + res.message);
              }
            })
          }).catch(() => {
      })
    };


    const saveAdd = () => {
      addForm.cronExpression = cronExpression.value
      let policy = {}
      let tempBackUpDataBase = []
      let tempExcludeDataBase = []
      for (let i = 1; i <= backUpDataBase.value.length; i++) {
        let el = backUpDataBase.value[i - 1]
        if (dataBase.value.includes(i)){
          tempBackUpDataBase.push(el)
        }else {
          tempExcludeDataBase.push(el)
        }
      }
      policy["backUpDataBase"] = tempBackUpDataBase
      policy["excludeDataBase"] = tempExcludeDataBase
      addForm.jobPolicy = JSON.stringify(policy)
      InsertBackUpJob(addForm).then((res) => {
        if (res.code === '0') {
          ElMessage.success("创建备份任务成功");
          getData()
        } else {
          ElMessage.error("创建备份任务失败" + res.message);
        }
      })
      addVisible.value = false;
    }

    const getStateTag = (value) => {
      if (value === 'NORMAL' || value === 'OFF') {
        return 'info'
      } else if (value === 'RUNNING') {
        return 'warning'
      } else if (value === 'SUCCESS' || value === 'ON') {
        return 'success'
      } else if (value === 'FAIL') {
        return 'danger'
      } else {
        return ''
      }
    }

    const jsonStrtoArray = (str) =>{
      return JSON.parse(str);
    }

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

    const lastRunTime = () =>{
      let cron = cronExpression.value
      LastRunTime(cron).then((res) => {
        if (res.code === '0') {
          lastRunTimeData.value = res.result
        } else {
          ElMessage.error("获取最近运行时间失败：" + res.message);
        }
      })
    }

    return {
      timer,
      cronExpression,
      lastRunTimeData,
      allDataBase,
      dataBase,
      dataSources,
      visible,
      deleteId,
      query,
      tableData,
      jobRecordData,
      pageTotal,
      editVisible,
      viewVisible,
      addVisible,
      form,
      addForm,
      saveEdit,
      lastRunTime,
      selectDataSource,
      allDataSource,
      handleEnable,
      filterEnable,
      handleAdd,
      handleSearch,
      handlePageChange,
      handleDelete,
      handleEdit,
      handleView,
      saveAdd,
      filterType,
      filterState,
      getStateTag,
      filterJobResult,
      filterJobRecordResult,
      jsonStrtoArray,
      byteToStr,
    };
  },
};
</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
}

.handle-select {
  width: 120px;
}

.handle-input {
  width: 300px;
  display: inline-block;
}

.table {
  width: 100%;
  font-size: 14px;
}

.red {
  color: #ff0000;
}

.mr10 {
  margin-right: 10px;
}

.table-td-thumb {
  display: block;
  margin: auto;
  width: 40px;
  height: 40px;
}
</style>
