package cc.focc.cavy.convert;


import cc.focc.cavy.model.entity.BackUpJob;
import cc.focc.cavy.model.entity.RestoreJob;
import cc.focc.cavy.model.vo.BackUpJobVO;
import cc.focc.cavy.model.vo.RestoreJobVO;

import java.util.List;

public class RestoreJobToVO extends AbstractConvert<RestoreJob, RestoreJobVO>{
    @Override
    public List<RestoreJobVO> convert(List<RestoreJob> restoreJobs) {
        return super.convert(restoreJobs);
    }

    @Override
    public RestoreJobVO convert(RestoreJob t) {
        RestoreJobVO v = new RestoreJobVO();
        v.setId(t.getId());
        v.setJobState(t.getJobState());
        v.setDataSourceId(t.getDataSourceId());
        v.setLog(t.getLog());
        v.setStartTime(t.getStartTime());
        v.setEndTime(t.getEndTime());
        return v;
    }

}
