<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <i class="el-icon-lx-cascades"></i>一次性备份任务
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container">
      <div class="handle-box">
        <el-select v-model="query.dataSourceId" placeholder="数据源" filterable clearable class="handle-input mr10">
          <el-option v-for="item in dataSources" :key="item.id" :label="item.sourceName" :value="item.id"></el-option>
        </el-select>
        <el-input v-model="query.jobName" placeholder="任务名称" class="handle-input mr10"></el-input>
        <el-select v-model="query.jobState" placeholder="任务状态" filterable clearable  class="handle-input mr10">
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
        <el-table-column prop="jobDescribe" label="描述" show-overflow-tooltip></el-table-column>
        <el-table-column
            prop="jobState"
            label="任务状态"
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
        <el-table-column prop="executeTime" label="执行时间"></el-table-column>
        <el-table-column label="操作"  align="center">
          <template #default="scope">
            <el-button icon="el-icon-view" size="small" @click="handleView(scope.$index, scope.row)"></el-button>
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
    <el-dialog title="新增一次性任务" v-model="addVisible" width="50%">
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

    <!-- job执行记录 -->
    <Job :viewVisible="viewVisible" :job-record-data="jobRecordData" :key="timer"></Job>

  </div>
</template>

<script>
import {ref, reactive} from "vue";
import {ElMessage, ElMessageBox,ElDrawer} from "element-plus";
import Job from "../../components/Job.vue";

import {
  AllDataBaseByDataSource,
  AllDataSource,
  BackUpJobRecordList,
  BackUpList,
  DeleteDataSource, InsertBackUpJob,
} from "../../api/index";


export default {
  name: "backUpTable",
  components: {Job},
  setup() {
    const timer = ref("")
    const defaultDataBase = ref(["information_schema","mysql","performance_schema","sys"])
    const dataSources = ref([])
    const dataBase = ref([])
    const allDataBase = ref([])
    const backUpDataBase = ref([])
    const visible = ref(false)
    const deleteId = ref(0)
    const query = reactive({
      jobName:"",
      jobState:"",
      dataSourceId:"",
      page: 1,
      size: 10,
      jobType: 0,
    });
    const tableData = ref([]);
    const jobRecordData = ref([]);
    const pageTotal = ref(0);
    const addVisible = ref(false);
    const addForm = reactive({
      jobName: "",
      jobDescribe: "",
      jobType: 0,
      dataSourceId: "",
      jobPolicy:"",
    });
    const editVisible = ref(false);
    const viewVisible = ref(false);
    let form = reactive({
      id:0,
      sourceName: "",
      sourceType: "",
      sourceHost: "",
      sourcePort: "",
      sourceUser: "",
      sourcePwd: "",
    });


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
      deleteId.value = row.id
      // 二次确认删除
      ElMessageBox.confirm("确定要删除吗？", "提示", {
        type: "warning",
      }).then(() => {
        DeleteDataSource(deleteId.value).then((res) => {
          if (res.code === '0') {
            ElMessage.success("删除数据源成功");
            getData()
          } else {
            ElMessage.error("删除数据源失败" + res.message);
          }
        })
        deleteId.value = 0
      }).catch(() => {
        deleteId.value = 0
      });
    };

    let idx = -1;
    const handleEdit = (index, row) => {
      idx = index;
      Object.keys(form).forEach((item) => {
        form[item] = row[item];
      });
      editVisible.value = true;
    };

    const handleView = (index, row) => {
      idx = index;
      BackUpJobRecordList(row.id).then((res) => {
        if (res.code === '0') {
          jobRecordData.value = res.result;
        } else {
          ElMessage.error("获取备份任务列表失败:" + res.message);
        }
      })
      timer.value = new Date().getTime()
      viewVisible.value = true;
    };

    const saveAdd = () => {
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
      if (value === 'NORMAL') {
        return 'info'
      } else if (value === 'RUNNING') {
        return 'warning'
      } else if (value === 'SUCCESS') {
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


    return {
      timer,
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
      selectDataSource,
      allDataSource,
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
