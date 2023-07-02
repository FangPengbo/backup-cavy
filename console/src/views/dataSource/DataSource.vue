<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <i class="el-icon-lx-cascades"></i> 数据源列表
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container">

      <div class="handle-box">
        <el-select v-model="query.sourceType" placeholder="类型" class="handle-input mr10">
          <el-option key="MYSQL" label="MYSQL" value="MYSQL"></el-option>
        </el-select>
        <el-input v-model="query.sourceName" placeholder="名称" class="handle-input mr10"></el-input>
        <el-input v-model="query.sourceHost" placeholder="地址" class="handle-input mr10"></el-input>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增</el-button>
      </div>

      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="id" label="ID" align="center"></el-table-column>
        <el-table-column prop="sourceName" label="名称"></el-table-column>
        <el-table-column
            prop="sourceType"
            label="类型"
            :filters="[
              { text: 'MYSQL', value: 'mysql' },
              ]"
            :filter-method="filterType"
            filter-placement="bottom-end"
        >
          <template #default="scope">
            <el-tag :type="scope.row.sourceType === 'mysql' ? 'success' : 'warning'">
              {{ scope.row.sourceType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sourceHost" label="地址"></el-table-column>
        <el-table-column prop="sourcePort" label="端口"></el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button icon="el-icon-edit" @click="handleEdit(scope.$index, scope.row)"></el-button>
            <el-button icon="el-icon-delete" class="red" @click="handleDelete(scope.$index, scope.row)"></el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination background layout="total, prev, pager, next" :current-page="query.page"
                       :page-size="query.size" :total="pageTotal"
                       @current-change="handlePageChange"></el-pagination>
      </div>

    </div>

    <!-- 编辑弹出框 -->
    <el-dialog title="编辑" v-model="editVisible" width="30%">
      <el-form label-width="70px">
        <el-form-item label="名称">
          <el-input v-model="form.sourceName"></el-input>
        </el-form-item>

        <el-form-item label="类型">
          <el-select v-model="form.sourceType" placeholder="请选择">
            <el-option key="bbk" label="MYSQL" value="mysql"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.sourceHost"></el-input>
        </el-form-item>

        <el-form-item label="端口">
          <el-input v-model="form.sourcePort"></el-input>
        </el-form-item>
        <el-form-item label="账户">
          <el-input v-model="form.sourceUser"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.sourcePwd" show-password/>
        </el-form-item>
      </el-form>
      <template #footer>
                <span class="dialog-footer">
                    <el-button @click="editVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveEdit">确 定</el-button>
                </span>
      </template>
    </el-dialog>

    <!-- 新增数据源 -->
    <el-dialog title="新增数据源" v-model="addVisible" width="30%">
      <el-form label-width="70px">
        <el-form-item label="名称">
          <el-input v-model="addForm.sourceName"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="region">
          <el-select v-model="addForm.sourceType" placeholder="请选择">
            <el-option key="mysql" label="MYSQL" value="mysql"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="addForm.sourceHost"></el-input>
        </el-form-item>
        <el-form-item label="端口">
          <el-input v-model="addForm.sourcePort"></el-input>
        </el-form-item>
        <el-form-item label="账户">
          <el-input v-model="addForm.sourceUser"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="addForm.sourcePwd" show-password/>
        </el-form-item>
      </el-form>
      <template #footer>
                <span class="dialog-footer">
                    <el-button @click="addVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveAdd">确 定</el-button>
                </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {ref, reactive} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {DataSource, DeleteDataSource, InsertDataSource, UpdateDataSource} from "../../api/index";

export default {
  name: "dataSourceTable",
  setup() {
    const deleteId = ref(0)
    const query = reactive({
      sourceName: "",
      sourceType: "",
      sourceHost: "",
      page: 1,
      size: 10
    });
    const tableData = ref([]);
    const pageTotal = ref(0);
    // 表格新增时弹窗和保存
    const addVisible = ref(false);
    const addForm = reactive({
      sourceName: "",
      sourceType: "mysql",
      sourceHost: "",
      sourcePort: "",
      sourceUser: "",
      sourcePwd: "",
    });
    // 表格编辑时弹窗和保存
    const editVisible = ref(false);
    let form = reactive({
      id:0,
      sourceName: "",
      sourceType: "",
      sourceHost: "",
      sourcePort: "",
      sourceUser: "",
      sourcePwd: "",
    });
    // 获取表格数据
    const getData = () => {
      DataSource(query).then((res) => {
        tableData.value = res.result.records;
        pageTotal.value = res.result.total || 50;
      });
    };
    getData();
    const filterType = (value, row) => {
      return row.sourceType === value
    }
    //新增操作
    const handleAdd = () => {
      addVisible.value = true;
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
    const saveEdit = () => {
      UpdateDataSource(form).then((res) => {
        if (res.code === '0') {
          ElMessage.success("修改数据源成功");
          getData()
        } else {
          ElMessage.error("修改数据源失败" + res.message);
        }
      })
      editVisible.value = false;
    };

    const saveAdd = () => {
      console.log(addForm)
      InsertDataSource(addForm).then((res) => {
        if (res.code === '0') {
          ElMessage.success("新增数据源成功");
          getData()
        } else {
          ElMessage.error("新增数据源失败" + res.message);
        }
      })
      addVisible.value = false;
    }
    return {
      deleteId,
      query,
      tableData,
      pageTotal,
      editVisible,
      addVisible,
      form,
      addForm,
      handleAdd,
      handleSearch,
      handlePageChange,
      handleDelete,
      handleEdit,
      saveEdit,
      saveAdd,
      filterType,
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
