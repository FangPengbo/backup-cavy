package cc.focc.cavy.core;

import cc.focc.cavy.constant.JobConstant;
import cc.focc.cavy.model.dto.SocketDataDTO;
import cc.focc.cavy.model.dto.SocketRestoreAtomData;
import cc.focc.cavy.model.dto.SocketRestoreData;
import cc.focc.cavy.model.entity.BackUpJobRecordAtom;
import cc.focc.cavy.model.entity.DataSource;
import cc.focc.cavy.model.entity.RestoreJob;
import cc.focc.cavy.model.entity.RestoreJobRecord;
import cc.focc.cavy.service.BackUpJobRecordAtomService;
import cc.focc.cavy.service.DataSourceService;
import cc.focc.cavy.service.RestoreJobRecordService;
import cc.focc.cavy.service.RestoreJobService;
import cc.focc.cavy.socket.RestoreSocket;
import cc.focc.cavy.util.AESUtil;
import cc.focc.cavy.util.ExecUtil;
import cc.focc.cavy.util.FileUtil;
import cc.focc.cavy.util.StringUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cc.focc.cavy.constant.BackUpConstant.*;

@Slf4j
@Component
public class RestoreJobHandle {

    @Value("${def.zip-key}")
    private String zipKey;

    @Autowired
    AESUtil aesUtil;

    @Autowired
    DataSourceService dataSourceService;

    @Autowired
    RestoreJobService restoreJobService;

    @Autowired
    RestoreJobRecordService restoreJobRecordService;

    @Autowired
    BackUpJobRecordAtomService atomService;

    public void restore(SocketDataDTO socketDataDTO) {
        String msgId = socketDataDTO.getMsgId();
        SocketRestoreData data = JSONUtil.toBean(JSONUtil.toJsonStr(socketDataDTO.getData()), SocketRestoreData.class);
        StringBuilder restoreLog = new StringBuilder();
        boolean result = true;
        Long jobId = 0L;
        try {
            Long dataSouceId = data.getDataSouceId();
            DataSource dataSource = dataSourceService.getById(dataSouceId);
            dataSource.setSourcePwd(AESUtil.decrypt(dataSource.getSourcePwd()));
            Set<Long> atomIds = data.getAtom().stream().map(SocketRestoreAtomData::getId).collect(Collectors.toSet());
            Map<Long, BackUpJobRecordAtom> atoms = atomService.listByIds(atomIds).stream().collect(Collectors.toMap(BackUpJobRecordAtom::getId, el -> el));
            jobId = saveRestoreJob(dataSource);
            StringUtil.infoLog(restoreLog, String.format("开始恢复%s数据源", dataSource.getSourceName()));
            result = startRestore(restoreLog, msgId, dataSource, jobId, data.getAtom(), atoms);
        } catch (Exception e) {
            log.error("restore err", e);
            StringUtil.errLog(restoreLog, e.getMessage());
            result = false;
        } finally {
            updateRestoreJob(jobId, result, restoreLog.toString());
        }
    }


    private void updateRestoreJob(Long jobId, Boolean result, String log) {
        RestoreJob job = new RestoreJob();
        job.setId(jobId);
        job.setJobState(result ? JobConstant.SUCCESS : JobConstant.FAIL);
        job.setLog(log);
        job.setEndTime(new Date());
        restoreJobService.updateById(job);
    }

    private boolean startRestore(StringBuilder restoreLog, String msgId, DataSource dataSource, Long jobId, List<SocketRestoreAtomData> atom, Map<Long, BackUpJobRecordAtom> atoms) {
        AtomicReference<Boolean> result = new AtomicReference<>(true);
        Map<Long, List<SocketRestoreAtomData>> collect = atom.stream().collect(Collectors.groupingBy(SocketRestoreAtomData::getId));
        for (Map.Entry<Long, List<SocketRestoreAtomData>> entry : collect.entrySet()) {
            Long recordId = entry.getKey();
            List<SocketRestoreAtomData> socketAtoms = entry.getValue();
            BackUpJobRecordAtom backUpJobRecordAtom = atoms.get(recordId);
            String filePath = backUpJobRecordAtom.getFilePath();
            File zipFile = new File(filePath);
            String newFileParent = zipFile.getParent() + File.separator + UUID.fastUUID() + File.separator;
            try {
                StringUtil.infoLog(restoreLog, String.format("解压备份文件：%s 到 %s", filePath, newFileParent));
                unpackFile(filePath, newFileParent);
                socketAtoms.forEach(el -> {
                    Date startTime = new Date();
                    String dataBase = el.getDataBase();
                    String table = el.getTable();
                    String tableFile = newFileParent + table + BACKUP_FILE_MYSQL_SUFFIX;
                    Pair<Boolean, String> restoreResult = restoreTable(restoreLog, dataSource, tableFile, dataBase, table);
                    if (!restoreResult.getKey()) {
                        result.set(false);
                    }
                    saveRestoreRecord(jobId, recordId, el, restoreResult, startTime);
                    sendRestoreResult(msgId, el, restoreResult);
                });
            }finally {
                FileUtil.deleteAll(newFileParent);
            }
        }
        return result.get();
    }

    private void sendRestoreResult(String msgId, SocketRestoreAtomData el, Pair<Boolean, String> restoreResult) {
        Map<String, Object> map = new HashMap<>();
        map.put("key", el.getKey());
        map.put("result", restoreResult.getKey());
        map.put("log", restoreResult.getValue());
        RestoreSocket.sendMessage(msgId, JSONUtil.toJsonStr(map));
    }

    private void saveRestoreRecord(Long jobId, Long recordId, SocketRestoreAtomData el, Pair<Boolean, String> restoreResult, Date startTime) {
        RestoreJobRecord record = new RestoreJobRecord();
        record.setRestoreJobId(jobId);
        record.setBackupJobRecordAtomId(recordId);
        record.setDataBase(el.getDataBase());
        record.setTableName(el.getTable());
        record.setLog(restoreResult.getValue());
        record.setStartTime(startTime);
        record.setEndTime(new Date());
        record.setJobState(restoreResult.getKey() ? JobConstant.SUCCESS : JobConstant.FAIL);
        restoreJobRecordService.save(record);
    }

    private Pair<Boolean, String> restoreTable(StringBuilder restoreLog, DataSource dataSource, String tableFile, String dataBase, String table) {
        StringBuilder restoreTableLog = new StringBuilder();
        String log1 = String.format("从备份文件 %s 恢复到[%s]数据库的[%s]表", tableFile, dataBase, table);
        StringUtil.infoLog(restoreLog, log1);
        StringUtil.infoLog(restoreTableLog, log1);
        boolean result = true;
        try {
            String createTableCmd = String.format(CMD_MYSQL_CREATE_DATABASE, dataSource.getSourceHost(), dataSource.getSourceUser(), dataSource.getSourcePort(), dataSource.getSourcePwd(), dataBase);
            log.debug("createDataBaseCmd:{}", createTableCmd);
            List<String> exec = ExecUtil.exec(createTableCmd);
            log.info(String.format("createDataBaseCmd res:%s",exec));
            StringUtil.infoLog(restoreTableLog, String.format("[%s]数据库不存在则创建", dataBase));
            String restoreCmd = String.format(CMD_MYSQL_RECOVER, dataSource.getSourceHost(), dataSource.getSourceUser(), dataSource.getSourcePort(), dataSource.getSourcePwd(), dataBase, tableFile);
            log.debug("restoreCmd:{}", restoreCmd);
            List<String> exec1 = ExecUtil.exec(restoreCmd);
            log.info(String.format("restoreCmd res:%s",exec1));
        } catch (Exception e) {
            log.error("restore table err", e);
            String errLog = String.format("从备份文件 %s 恢复到[%s]数据库的[%s]表失败：%s", tableFile, dataBase, table, e.getMessage());
            StringUtil.errLog(restoreLog, errLog);
            StringUtil.errLog(restoreTableLog, errLog);
            result = false;
        }
        String log2 = String.format("恢复[%s]数据库的[%s]表完成", dataBase, table);
        StringUtil.infoLog(restoreLog, log2);
        StringUtil.infoLog(restoreTableLog, log2);
        return Pair.of(result, restoreTableLog.toString());
    }

    private void unpackFile(String filePath, String newFileParent) {
        String unpackCmd = String.format(CMD_ZIP_UNPACK, zipKey, filePath, newFileParent);
        log.debug("unpackCmd : {}", unpackCmd);
        List<String> exec = ExecUtil.exec(unpackCmd);
        log.info(String.format("unpackCmd res:%s",exec));
    }


    private Long saveRestoreJob(DataSource dataSource) {
        RestoreJob job = new RestoreJob();
        job.setDataSourceId(dataSource.getId());
        restoreJobService.save(job);
        return job.getId();
    }


}
