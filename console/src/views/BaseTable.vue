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
                <el-input v-model="query.keyWord" placeholder="keyWord" class="handle-input mr10"></el-input>
                <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
              <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增</el-button>
            </div>

            <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
                <el-table-column prop="id" label="ID" align="center"></el-table-column>
                <el-table-column prop="sourceName" label="名称"></el-table-column>
<!--                <el-table-column prop="web_site" label="网站" >-->
<!--                  <template #default="scope">-->
<!--                    <a :href="scope.row.web_site" target="_blank" class="buttonText">{{scope.row.web_site}}</a>-->
<!--                  </template>-->
<!--                  <template #default="scope">-->
<!--                    <el-button type="text" icon="el-icon-view" @click="handleView(scope.row.encryption_pwd)"></el-button>-->
<!--                  </template>-->
<!--                </el-table-column>-->
                <el-table-column prop="sourceType" label="类型"></el-table-column>
                <el-table-column prop="sourceHost" label="地址"></el-table-column>
                <el-table-column prop="sourcePort" label="端口"></el-table-column>

                <el-table-column label="操作" width="180" align="center">
                    <template #default="scope">
                        <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.$index, scope.row)"></el-button>
                        <el-button type="text" icon="el-icon-delete" class="red" @click="handleDelete(scope.$index, scope.row)"></el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination">
                <el-pagination background layout="total, prev, pager, next" :current-page="query.page_num"
                    :page-size="query.page_size" :total="pageTotal" @current-change="handlePageChange"></el-pagination>
            </div>

        </div>

        <!-- 编辑弹出框 -->
        <el-dialog title="编辑" v-model="editVisible" width="30%">
            <el-form label-width="70px">
                <el-form-item label="站点">
                    <el-input v-model="form.web_name"></el-input>
                </el-form-item>
                <el-form-item label="网站">
                    <el-input v-model="form.web_site"></el-input>
                </el-form-item>
                <el-form-item label="关键词">
                  <el-input v-model="form.key_word"></el-input>
                </el-form-item>
                <el-form-item label="账户">
                  <el-input v-model="form.account"></el-input>
                </el-form-item>
                <el-form-item label="密码">
                  <el-input v-model="form.encryption_pwd"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="editVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveEdit">确 定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 新增弹出框 -->
        <el-dialog title="新增" v-model="addVisible" width="30%">
        <el-form label-width="70px">
          <el-form-item label="站点">
            <el-input v-model="addForm.web_name"></el-input>
          </el-form-item>
          <el-form-item label="网站">
            <el-input v-model="addForm.web_site"></el-input>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="addForm.key_word"></el-input>
          </el-form-item>
          <el-form-item label="账户">
            <el-input v-model="addForm.account"></el-input>
          </el-form-item>
          <el-form-item label="密码">
            <el-input type="password" v-model="addForm.encryption_pwd" show-password/>
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
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {fetchData, CopyPwd, InsertRecord, dataSource} from "../api/index";

export default {
    name: "dataSourceTable",
    setup() {
        const query = reactive({
          keyWord: "",
          page: 1,
          size: 10
        });
        const tableData = ref([]);
        const pageTotal = ref(0);
        // 获取表格数据
        const getData = () => {
            dataSource(query).then((res) => {
                tableData.value = res.result.records;
                pageTotal.value = res.result.total || 50;
            });
        };
        getData();
        // 表格新增时弹窗和保存
        const addVisible = ref(false);
        let addForm = reactive({
          web_name: "",
          web_site: "",
          account: "",
          encryption_pwd: "",
          key_word: "",
        });
        //新增操作
        const handleAdd = () =>{
          addVisible.value = true;

        }
        // 查询操作
        const handleSearch = () => {
            query.page_num = 1;
            getData();
        };
        // 分页导航
        const handlePageChange = (val) => {
            query.page_num = val;
            getData();
        };

        // 删除操作
        const handleDelete = (index) => {
            // 二次确认删除
            ElMessageBox.confirm("确定要删除吗？", "提示", {
                type: "warning",
            })
                .then(() => {
                    ElMessage.success("删除成功");
                    tableData.value.splice(index, 1);
                })
                .catch(() => {});
        };

        // 表格编辑时弹窗和保存
        const editVisible = ref(false);
        let form = reactive({
          web_name: "",
          web_site: "",
          account: "",
          encryption_pwd: "",
          key_word: "",
        });
        let idx = -1;
        const handleEdit = (index, row) => {
            idx = index;
            Object.keys(form).forEach((item) => {
                form[item] = row[item];
            });
            form["encryption_pwd"] = ""
            editVisible.value = true;
        };
        const saveEdit = () => {
            editVisible.value = false;
            ElMessage.success(`修改第 ${idx + 1} 行成功`);
            Object.keys(form).forEach((item) => {
                tableData.value[idx][item] = form[item];
            });
        };

        const saveAdd = () => {
          console.log(addForm)
          InsertRecord(addForm).then((res) =>{
            if (res.Code === 0){
              ElMessage.success("新增记录成功");
              getData()
            }else {
              ElMessage.error("新增记录失败" + res.Message);
              return
            }
          })
          addVisible.value = false;
        }

      // Copy密码
      const handleView = (pwd) => {
        console.log(pwd)
        CopyPwd(pwd).then((res) => {
          if (res.Code === 0){
            //创建input标签
            var input = document.createElement("input");     // 直接构建input
            input.value = res.Data;   // 设置内容
            document.body.appendChild(input);        // 添加临时实例
            input.select();      // 选择实例内容
            document.execCommand("Copy");     // 执行复制
            document.body.removeChild(input);  // 删除临时实例
            ElMessage.success("Copy成功");
          }else {
            ElMessage.error("Copy失败" + res.Message);
          }
        });
      };
        return {
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
            handleView,
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
