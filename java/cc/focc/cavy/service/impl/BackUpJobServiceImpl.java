package cc.focc.cavy.service.impl;

import cc.focc.cavy.constant.JobState;
import cc.focc.cavy.constant.JobType;
import cc.focc.cavy.convert.BackUpJobVOToBackUpJob;
import cc.focc.cavy.convert.DataSourceToDataSourceVO;
import cc.focc.cavy.convert.DataSourceVOToDataSource;
import cc.focc.cavy.exception.BizException;
import cc.focc.cavy.mapper.BackUpJobMapper;
import cc.focc.cavy.model.entity.BackUpJob;
import cc.focc.cavy.model.entity.DataSource;
import cc.focc.cavy.model.vo.BackUpJobVO;
import cc.focc.cavy.model.vo.DataSourceVO;
import cc.focc.cavy.service.BackUpJobService;
import cc.focc.cavy.util.AESUtil;
import cc.focc.cavy.core.BackUpJobHandle;
import cc.focc.cavy.util.CronUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class BackUpJobServiceImpl extends ServiceImpl<BackUpJobMapper, BackUpJob> implements BackUpJobService {

    public static final BackUpJobVOToBackUpJob  backUpJobVOToBackUpJob = new BackUpJobVOToBackUpJob();

    @Resource(name = "backUpJobPool")
    private ThreadPoolExecutor backUpJobPool;

    @Lazy
    @Autowired
    private BackUpJobHandle backUpJobHandle;

    @Autowired
    private BackUpJobMapper backUpJobMapper;

    @Override
    public Boolean save(BackUpJobVO backUpJobVO) {
        if (JobType.NORMAL_JOB.equals(backUpJobVO.getJobType())){
            backUpJobVO.setJobState(JobState.RUNNING);
        }else if (JobType.CRON_JOB.equals(backUpJobVO.getJobType())){
            backUpJobVO.setJobState(JobState.NORMAL);
            if (!CronUtils.isValid(backUpJobVO.getCronExpression())){
                throw new BizException("-1","cron表达式有误");
            }
            backUpJobVO.setNextTime(CronUtils.getNextRunTime(backUpJobVO.getCronExpression(),1).get(0));
        }else {
            throw new BizException(String.format("未知的任务类型:%s",backUpJobVO.getJobType()));
        }
        BackUpJob backUpJob = backUpJobVOToBackUpJob.convert(backUpJobVO);
        backUpJobMapper.insert(backUpJob);
        if (JobType.NORMAL_JOB.equals(backUpJobVO.getJobType())){
            backUpJobPool.execute(() -> backUpJobHandle.runJob(backUpJob));
        }
        return true;
    }

    @Override
    public List<BackUpJob> getLessNowDateJob() {
        return backUpJobMapper.getLessNowDateJob(new Date());
    }
}
