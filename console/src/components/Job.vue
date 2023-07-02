<template>

  <el-drawer title="job执行记录" :model-value="viewVisible" :show-close="true" size="80%">
    <el-button icon="el-icon-refresh" type="primary" style="float: right;margin: 10px" size="small" @click="jumpRestorePage" >选中恢复</el-button>
    <el-table :data="jobRecordData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
      <el-table-column type="expand">
        <template #default="props">
          <div>
            <el-table :data="props.row.atomVOList" :border="childBorder" @select-all="handleSelectionAll"  @select="handleSelectionChange">
              <el-table-column type="selection" width="55" />
              <el-table-column label="数据库" prop="dataBase" />
              <el-table-column
                  prop="result"
                  label="备份结果"
                  :filters="[
                      { text: '成功', value: 'SUCCESS' },
                      { text: '失败', value: 'FAIL' },
                      ]"
                  :filter-method="filterJobRecordResult"
                  filter-placement="bottom-end"
              >
                <template #default="scope">
                  <el-tag :type="getStateTag(scope.row.result)">
                    {{ scope.row.result }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="备份路径" min-width="200" prop="filePath" />
              <el-table-column label="备份大小">
                <template #default="scope">
                  <span>{{ byteToStr(scope.row.fileSize) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="开始时间" min-width="100" prop="startTime" />
              <el-table-column label="结束时间" min-width="100" prop="endTime" />
              <el-table-column label="备份表" align="center">
                <template #default="scope">
                  <el-popover placement="left" :width="300">
                    <template #reference>
                      <el-button slot="reference" icon="el-icon-more"></el-button>
                    </template>
                    <div style="height: 300px; width: 300px; overflow: auto">
                      <el-checkbox-group v-model="checkList[scope.row.id]">
                        <el-checkbox style="display: block; padding-top: 10px" v-for="table in jsonStrtoArray(scope.row.tables)" :label="table" />
                      </el-checkbox-group>
                    </div>
                  </el-popover>
                </template>
              </el-table-column>
              <el-table-column label="查看日志" align="center">
                <template #default="scope">
                  <el-popover placement="left" :width="480">
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
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" align="center"></el-table-column>
      <el-table-column
          prop="jobResult"
          label="执行状态"
          :filters="[
              { text: '成功', value: 'SUCCESS' },
              { text: '失败', value: 'FAIL' },
              ]"
          :filter-method="filterJobResult"
          filter-placement="bottom-end"
      >
        <template #default="scope">
          <el-tag :type="getStateTag(scope.row.jobResult)">
            {{ scope.row.jobResult }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="jobStartTime" label="开始时间"></el-table-column>
      <el-table-column prop="jobEndTime" label="结束时间"></el-table-column>
    </el-table>
    <el-drawer title="恢复页面" v-model="restoreViewVisible" :show-close="true" size="50%" :append-to-body="true">
      <el-form-item style="float: right;margin-right: 4em">
          <span slot="label">
            <i class="el-icon-coin" style="color: red" >* </i>
            目标数据源
          </span>
        <el-select v-model="selectDataSourceId" placeholder="选择目标数据源" filterable>
          <el-option v-for="item in dataSources" :key="item.id" :label="item.sourceName" :value="item.id"></el-option>
        </el-select>
        <el-button type="primary" @click="startRestore" style="margin-left: 1em" :disabled = startRestoreButton>开始</el-button>
      </el-form-item>
      <el-table :data="selectAtom" border class="table" ref="multipleTable" header-cell-class-name="table-header" max-height="600">
        <el-table-column prop="key" label="序列" align="center"></el-table-column>
        <el-table-column prop="dataBase" label="数据库" show-overflow-tooltip></el-table-column>
        <el-table-column prop="table" label="表" show-overflow-tooltip></el-table-column>
        <el-table-column prop="percentage" label="进度" >
          <template #default="scope">
            <el-progress :percentage="scope.row.percentage" :status="scope.row.status"/>
          </template>
        </el-table-column>
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
  </el-drawer>

</template>
<script>
import {ref} from "vue";
import {AllDataSource, SW_BASE_URL} from "../api";
import {ElMessage} from "element-plus";


export default {

  props: [
    'viewVisible',
    'jobRecordData',
  ],

  watch:{
    jobRecordData:{
      immediate: true,
      handler (val) {
        for (let i = 0; i < val.length; i++) {
          let data = val[i]
          let atoms = data.atomVOList
          for (let j = 0; j < atoms.length; j++){
            let atomData = atoms[j]
            this.checkList[atomData.id] = []
            this.allAtom[atomData.id] = atomData
          }
        }
      }
    },
    viewVisible:{
      immediate: true,
      handler (val) {
        console.log("viewVisible" + val)
      }
    }
  },
  setup() {

    const startRestoreButton = ref(false)
    const dataSources = ref([])
    const restoreViewVisible = ref(false);
    const checkList = ref({})
    const allAtom = ref({})
    const selectDataSourceId = ref()
    const multipleSelection = ref([])
    const selectAtom = ref([])
    const ws = ref()

    const startRestore = () => {
      console.log(selectDataSourceId)
      console.log(selectAtom)
      if (selectDataSourceId.value === undefined){
        ElMessage.error("请选择目标数据源");
        return
      }
      startRestoreButton.value = true
      let msgId = uuid()
      ws.value = new WebSocket(SW_BASE_URL + "/restore/" + msgId );

      ws.value.onopen = function(evt) {
        console.log("连接成功")
        ws.value.send(JSON.stringify({"msgId":msgId,"data":{"dataSouceId":selectDataSourceId.value,"atom":selectAtom.value}}));
      };

      ws.value.onmessage = function(evt) {
        let res = JSON.parse(evt.data)
        if (res.result){
          selectAtom.value[res.key -1].status = "success"
        }else {
          selectAtom.value[res.key -1].status = "exception"
        }
        selectAtom.value[res.key -1].percentage = 100
        selectAtom.value[res.key -1].log = res.log
      };

      ws.value.onError = function(evt) {
        ElMessage.error("websocket连接失败:" + evt);
      };
    }

    const handleSelectionChange = (val,row) => {
      let remove = true
      let rowId = row.id
      for (let i = 0; i < val.length ;i++){
        let el =  val[i]
        let id = el.id
        if (id == rowId){
          remove = false
        }
        let tables = jsonStrtoArray(el.tables)
        checkList.value[id] = tables
      }
      if (remove){
        checkList.value[rowId] = []
      }
      multipleSelection.value = val
    }

    const handleSelectionAll = (val) => {
      if (val.length === 0){
        //todo: 清空全选
        console.log("清空全选")
      }else {
        for (let i = 0; i < val.length ;i++){
          let el =  val[i]
          let id = el.id
          let tables = jsonStrtoArray(el.tables)
          checkList.value[id] = tables
        }
      }
      multipleSelection.value = val
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

    const filterJobRecordResult = (value, row) => {
      return row.result === value
    }

    const jumpRestorePage = () => {
      selectAtom.value = []
      allDataSource()
      let index = 1
      for(let el in checkList.value) {
        let value = checkList.value[el]
        if (value.length > 0){
          for(let table in value){
            let dataBaseName = ""
            for (let allAtomKey in allAtom.value) {
              if (el == allAtom.value[allAtomKey].id){
                dataBaseName = allAtom.value[allAtomKey].dataBase
                break;
              }
            }
            selectAtom.value.push({
              key: index,
              id: el,
              dataBase: dataBaseName,
              table:value[table],
              percentage:0,
              status:"",
              result:0,
              log:""
            })
            index++;
          }
        }
      }
      restoreViewVisible.value = true;
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

    const uuid = () => {
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = Math.random() * 16 | 0
        const v = c === 'x' ? r : (r & 0x3 | 0x8)
        return v.toString(16)
      })
    }

    const filterJobResult = (value, row) => {
      return row.jobResult === value
    }


    return{
      allAtom,
      selectAtom,
      startRestoreButton,
      selectDataSourceId,
      checkList,
      restoreViewVisible,
      dataSources,
      filterJobResult,
      startRestore,
      jsonStrtoArray,
      byteToStr,
      getStateTag,
      filterJobRecordResult,
      handleSelectionChange,
      jumpRestorePage,
      handleSelectionAll,
    };

  },
};
</script>
<style scoped>

</style>
