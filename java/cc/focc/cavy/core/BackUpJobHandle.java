package cc.focc.cavy.core;

import cc.focc.cavy.constant.DataSourceConstant;
import cc.focc.cavy.model.dto.BackUpDataBaseResult;
import cc.focc.cavy.model.dto.BackUpJobResult;
import cc.focc.cavy.model.dto.JobPolicy;
import cc.focc.cavy.model.entity.BackUpJob;
import cc.focc.cavy.model.entity.BackUpJobRecord;
import cc.focc.cavy.model.entity.BackUpJobRecordAtom;
import cc.focc.cavy.model.entity.DataSource;
import cc.focc.cavy.service.BackUpJobRecordAtomService;
import cc.focc.cavy.service.BackUpJobRecordService;
import cc.focc.cavy.service.BackUpJobService;
import cc.focc.cavy.service.DataSourceService;
import cc.focc.cavy.util.*;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import static cc.focc.cavy.constant.BackUpConstant.*;
import static cc.focc.cavy.constant.JobConstant.*;

@Slf4j
@Component
public class BackUpJobHandle {

    @Value("${def.zip-key}")
    private String zipKey;

    @Resource(name = "backUpJobPool")
    private ThreadPoolExecutor backUpJobPool;

    @Autowired
    DataSourceService dataSourceService;

    @Autowired
    BackUpJobService backUpJobService;

    @Autowired
    BackUpJobRecordService backUpJobRecordService;

    @Autowired
    BackUpJobRecordAtomService backUpJobRecordAtomService;

    @Autowired
    AESUtil aesUtil;

    public void runJob(BackUpJob job) {
        Long dataSourceId = job.getDataSourceId();
        DataSource dataSource = dataSourceService.selectOneByPrimary(dataSourceId);
        BackUpJobResult jobResult = BackUpJobResult.builder().startTime(new Date()).build();
        if (DataSourceConstant.MYSQL.equals(dataSource.getSourceType())) {
            jobResult = mysqlBackUp(job, dataSource);
        }
        jobResult.setEndTime(new Date());
        runJobAfter(job, jobResult);
    }

    private void runJobAfter(BackUpJob job, BackUpJobResult jobResult) {
        Long jobId = job.getId();
        Boolean result = jobResult.getResult();
        String message = jobResult.getMessage();
        Date startTime = jobResult.getStartTime();
        Date endTime = jobResult.getEndTime();

        BackUpJob backUpJob = backUpJobService.getById(jobId);
        BackUpJob updateJob = new BackUpJob();
        if (CRON_JOB.equals(backUpJob.getJobType())){
            updateJob.setLastTime(backUpJob.getNextTime());
            updateJob.setNextTime(CronUtils.getNextRunTime(backUpJob.getCronExpression(),1).get(0));
        }
        updateJob.setId(jobId);
        updateJob.setExecuteTime(startTime);
        updateJob.setJobState(result ? SUCCESS : FAIL);
        backUpJobService.updateById(updateJob);

        BackUpJobRecord jobRecord = new BackUpJobRecord();
        jobRecord.setJobId(jobId);
        jobRecord.setJobResult(result ? SUCCESS : FAIL);
        jobRecord.setJobMessage(message);
        jobRecord.setStartTime(startTime);
        jobRecord.setEndTime(endTime);
        backUpJobRecordService.save(jobRecord);

        Long recordId = jobRecord.getId();
        List<BackUpDataBaseResult> backUpDataBaseResults = jobResult.getBackUpDataBaseResults();
        List<BackUpJobRecordAtom> atoms = backUpDataBaseResults.stream().map(el -> {
            BackUpJobRecordAtom atom = new BackUpJobRecordAtom();
            BeanUtil.copyProperties(el, atom);
            atom.setRecordId(recordId);
            atom.setTables(JSONUtil.toJsonStr(el.getTables()));
            atom.setResult(el.getResult() ? SUCCESS : FAIL);
            return atom;
        }).toList();
        backUpJobRecordAtomService.saveBatch(atoms);
    }

    private BackUpJobResult mysqlBackUp(BackUpJob job, DataSource dataSource){
        BackUpJobResult jobResult = BackUpJobResult.builder().startTime(new Date()).result(true).build();
        String sourceHost = dataSource.getSourceHost();
        Integer sourcePort = dataSource.getSourcePort();
        String sourceUser = dataSource.getSourceUser();
        dataSource.setSourcePwd(AESUtil.decrypt(dataSource.getSourcePwd()));
        String sourcePwd = dataSource.getSourcePwd();
        JobPolicy jobPolicy = JSONUtil.toBean(job.getJobPolicy(), JobPolicy.class);
        List<String> backUpDataBase = jobPolicy.getBackUpDataBase();
        List<String> excludeDataBase = jobPolicy.getExcludeDataBase();
        try {
            if (CollectionUtil.isNotEmpty(backUpDataBase)) {
                backUpDataBase.removeAll(excludeDataBase);
            } else {
                backUpDataBase = MySqlUtil.getAllDataBase(sourceHost, sourcePort, sourceUser, sourcePwd, excludeDataBase);
            }
            List<Callable<BackUpDataBaseResult>> tasks = new ArrayList<>();
            for (String dataBase : backUpDataBase) {
                tasks.add(() -> mysqlBackupJob(dataSource, dataBase));
            }
            List<Future<BackUpDataBaseResult>> futures = backUpJobPool.invokeAll(tasks);
            List<BackUpDataBaseResult> results = new ArrayList<>();
            for (Future<BackUpDataBaseResult> future : futures) {
                results.add(future.get());
            }
            for (BackUpDataBaseResult result : results) {
                if (!result.getResult()) {
                    jobResult.setResult(false);
                }
            }
            jobResult.setBackUpDataBaseResults(results);
        }catch (Exception e){
            jobResult.setResult(false);
            jobResult.setMessage(e.getMessage());
        }
        return jobResult;
    }

    private BackUpDataBaseResult mysqlBackupJob(DataSource dataSource, String dataBase) {
        String sourceHost = dataSource.getSourceHost();
        Integer sourcePort = dataSource.getSourcePort();
        String sourceUser = dataSource.getSourceUser();
        String sourcePwd = dataSource.getSourcePwd();
        BackUpDataBaseResult result = BackUpDataBaseResult.builder().result(true).startTime(new Date()).build();
        StringBuilder backUpLog = new StringBuilder();
        String backUpFilePrefix = BACKUP_FILE_PREFIX + File.separator + "mysql" + File.separator + sourceHost + File.separator + DatePattern.PURE_DATETIME_FORMAT.format(new Date()) + File.separator + dataBase;
        String createDirCmd = String.format(CMD_CREATE_DIR, backUpFilePrefix);
        ExecUtil.exec(createDirCmd);
        StringUtil.infoLog(backUpLog, String.format("开始备份[%s]数据库", dataBase));
        String zipFilePath = backUpFilePrefix + BACKUP_FILE_ZIP_SUFFIX;
        List<String> tables = new ArrayList<>();
        try {
            tables = MySqlUtil.getAllTable(sourceHost, sourcePort, sourceUser, sourcePwd, dataBase, null);
            StringUtil.infoLog(backUpLog, String.format("获取[%s]数据库的所有待备份的表[%s]", dataBase, tables));
            for (String table : tables) {
                try {
                    String targetFile = backUpFilePrefix + File.separator + table + BACKUP_FILE_MYSQL_SUFFIX;
                    String dumpCmd = String.format(CMD_MYSQL_DUMP_TABLE, sourceHost, sourceUser, sourcePort, sourcePwd, dataBase, table, targetFile);
                    log.debug("dumpCmd : {}", dumpCmd);
                    StringUtil.infoLog(backUpLog, String.format("开始备份[%s]数据库的[%s]表", dataBase, table));
                    List<String> dumpResult = ExecUtil.exec(dumpCmd);
                    String hasError = MySqlUtil.hasError(dumpResult);
                    if (StrUtil.isNotEmpty(hasError)){
                        throw new RuntimeException(hasError);
                    }
                    StringUtil.infoLog(backUpLog, String.format("完成备份[%s]数据库的[%s]表", dataBase, table));
                }catch (Exception e){
                    String errLog = String.format("备份[%s]数据库的[%s]表失败:[%s]", dataBase, table,e.getMessage());
                    result.setResult(false);
                    StringUtil.errLog(backUpLog, errLog);
                    log.error(errLog);
                }
            }
            String zipCmd = String.format(CMD_ZIP_COMPRESS, zipKey, zipFilePath, backUpFilePrefix);
            log.debug("zipCmd : {}", zipCmd);
            ExecUtil.exec(zipCmd);
            StringUtil.infoLog(backUpLog, String.format("完成备份[%s]数据库", dataBase));
        }catch (Exception e){
            String errLog = String.format("备份[%s]数据库失败:[%s]", dataBase, e.getMessage());
            result.setResult(false);
            StringUtil.errLog(backUpLog, errLog);
            log.error(errLog);
        } finally {
            FileUtil.deleteAll(backUpFilePrefix);
        }
        result.setDataBase(dataBase);
        result.setFileSize(FileUtil.computeFileSize(zipFilePath));
        result.setFilePath(zipFilePath);
        result.setLog(backUpLog.toString());
        result.setTables(tables);
        result.setEndTime(new Date());
        return result;
    }


}
