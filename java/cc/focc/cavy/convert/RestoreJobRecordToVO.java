package cc.focc.cavy.convert;


import cc.focc.cavy.model.entity.RestoreJob;
import cc.focc.cavy.model.entity.RestoreJobRecord;
import cc.focc.cavy.model.vo.RestoreJobRecordVO;
import cc.focc.cavy.model.vo.RestoreJobVO;

import java.util.List;

public class RestoreJobRecordToVO extends AbstractConvert<RestoreJobRecord, RestoreJobRecordVO>{
    @Override
    public List<RestoreJobRecordVO> convert(List<RestoreJobRecord> ins) {
        return super.convert(ins);
    }

    @Override
    public RestoreJobRecordVO convert(RestoreJobRecord t) {
        RestoreJobRecordVO v = new RestoreJobRecordVO();
        v.setId(t.getId());
        v.setRestoreJobId(t.getRestoreJobId());
        v.setBackupJobRecordAtomId(t.getBackupJobRecordAtomId());
        v.setDataBase(t.getDataBase());
        v.setTableName(t.getTableName());
        v.setJobState(t.getJobState());
        v.setLog(t.getLog());
        v.setStartTime(t.getStartTime());
        v.setEndTime(t.getEndTime());
        return v;
    }

}
