<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <i class="el-icon-lx-cascades"></i>恢复记录
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="container">
      <div class="handle-box">
        <el-select v-model="query.dataSourceId" placeholder="数据源" filterable clearable class="handle-input mr10">
          <el-option v-for="item in dataSources" :key="item.id" :label="item.sourceName" :value="item.id"></el-option>
        </el-select>
        <el-select v-model="query.jobState" placeholder="任务状态" filterable clearable  class="handle-input mr10">
          <el-option key="NORMAL" label="正常" value="NORMAL"></el-option>
          <el-option key="RUNNING" label="运行中" value="RUNNING"></el-option>
          <el-option key="SUCCESS" label="成功" value="SUCCESS"></el-option>
          <el-option key="FAIL" label="失败" value="FAIL"></el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
      </div>
      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="id" label="ID" align="center"></el-table-column>
        <el-table-column prop="dataSourceName" label="数据源名称"></el-table-column>
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
        <el-table-column prop="startTime" label="开始时间"></el-table-column>
        <el-table-column prop="endTime" label="结束时间"></el-table-column>
        <el-table-column label="操作" align="center">
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

    <!-- 恢复记录 -->
    <el-drawer
        size="50%"
        v-model="viewVisible"
        title="恢复记录"
    >
      <el-table :data="jobRecordData" border ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column label="ID" prop="id"/>
        <el-table-column label="数据库" prop="dataBase"/>
        <el-table-column label="表" prop="tableName"/>
        <el-table-column label="恢复结果" prop="jobState"
                         :filters="[
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
        <el-table-column label="开始时间" min-width="100" prop="startTime"/>
        <el-table-column label="结束时间" min-width="100" prop="endTime"/>
        <el-table-column label="查看日志" align="center">
          <template #default="scope">
            <el-popover placement="left" :width="500">
              <template #reference>
                <el-button slot="reference" icon="el-icon-view"></el-button>
              </template>
              <div style="height: 500px; white-space: pre-line; overflow: auto">
                {{ scope.row.log }}
              </div>
            </el-popover>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>

</template>

<script>
import {ref, reactive} from "vue";
import {ElMessage, ElDrawer} from "element-plus";
import {
  AllDataSource,
  RestoreJobRecordList, RestoreList,
} from "../../api/index";

export default {
  name: "restoreTable",
  setup() {
    const dataSources = ref([])
    const query = reactive({
      jobState:"",
      dataSourceId:"",
      page: 1,
      size: 10,
    });
    const tableData = ref([]);
    const jobRecordData = ref([]);
    const pageTotal = ref(0);
    const viewVisible = ref(false);

    // 获取表格数据
    const getData = () => {
      RestoreList(query).then((res) => {
        if (res.code === '0') {
          allDataSource()
          tableData.value = res.result.records;
          pageTotal.value = res.result.total || 50;
        } else {
          ElMessage.error("获取恢复任务列表失败" + res.message);
        }
      });
    };

    getData();

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

    const handleView = (index, row) => {
      RestoreJobRecordList(row.id).then((res) => {
        if (res.code === '0') {
          jobRecordData.value = res.result;
        } else {
          ElMessage.error("获取恢复任务详情失败" + res.message);
        }
      })
      viewVisible.value = true;
    };

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

    const filterState = (value, row) => {
      return row.jobState === value
    }

    return {
      dataSources,
      query,
      tableData,
      jobRecordData,
      pageTotal,
      viewVisible,
      handleSearch,
      handlePageChange,
      handleView,
      filterState,
      getStateTag,
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
