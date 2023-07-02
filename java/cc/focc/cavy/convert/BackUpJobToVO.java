package cc.focc.cavy.convert;


import cc.focc.cavy.model.entity.BackUpJob;
import cc.focc.cavy.model.vo.BackUpJobVO;

import java.util.List;

public class BackUpJobToVO extends AbstractConvert<BackUpJob, BackUpJobVO>{
    @Override
    public List<BackUpJobVO> convert(List<BackUpJob> BackUpJobVOS) {
        return super.convert(BackUpJobVOS);
    }

    @Override
    public BackUpJobVO convert(BackUpJob t) {
        BackUpJobVO v = new BackUpJobVO();
        v.setId(t.getId());
        v.setJobName(t.getJobName());
        v.setJobDescribe(t.getJobDescribe());
        v.setJobType(t.getJobType());
        v.setJobState(t.getJobState());
        v.setJobEnable(t.getJobEnable());
        v.setDataSourceId(t.getDataSourceId());
        v.setCronExpression(t.getCronExpression());
        v.setExecuteTime(t.getExecuteTime());
        v.setLastTime(t.getLastTime());
        v.setNextTime(t.getNextTime());
        v.setCreateTime(t.getCreateTime());
        v.setJobPolicy(t.getJobPolicy());
        return v;
    }

}
