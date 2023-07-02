package cc.focc.cavy.service.impl;

import cc.focc.cavy.constant.JobConstant;
import cc.focc.cavy.convert.BackUpJobToVO;
import cc.focc.cavy.convert.BackUpJobVOTo;
import cc.focc.cavy.exception.BizException;
import cc.focc.cavy.mapper.BackUpJobMapper;
import cc.focc.cavy.model.entity.BackUpJob;
import cc.focc.cavy.model.entity.BackUpJobRecord;
import cc.focc.cavy.model.entity.BackUpJobRecordAtom;
import cc.focc.cavy.model.entity.DataSource;
import cc.focc.cavy.model.vo.BackUpJobAtomVO;
import cc.focc.cavy.model.vo.BackUpJobRecordAtomVO;
import cc.focc.cavy.model.vo.BackUpJobVO;
import cc.focc.cavy.service.BackUpJobRecordAtomService;
import cc.focc.cavy.service.BackUpJobRecordService;
import cc.focc.cavy.service.BackUpJobService;
import cc.focc.cavy.core.BackUpJobHandle;
import cc.focc.cavy.service.DataSourceService;
import cc.focc.cavy.util.CronUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static cc.focc.cavy.constant.JobConstant.*;

@Slf4j
@Service
public class BackUpJobServiceImpl extends ServiceImpl<BackUpJobMapper, BackUpJob> implements BackUpJobService {

    public static final BackUpJobVOTo BACK_UP_JOB_VO_TO = new BackUpJobVOTo();
    public static final BackUpJobToVO BACK_UP_JOB_TO_VO = new BackUpJobToVO();

    @Resource(name = "backUpJobPool")
    private ThreadPoolExecutor backUpJobPool;

    @Lazy
    @Autowired
    private BackUpJobHandle backUpJobHandle;

    @Autowired
    private BackUpJobMapper backUpJobMapper;

    @Autowired
    private BackUpJobRecordService backUpJobRecordService;

    @Autowired
    private BackUpJobRecordAtomService backUpJobRecordAtomService;

    @Autowired
    private DataSourceService dataSourceService;

    @Override
    public Boolean save(BackUpJobVO backUpJobVO) {
        if (NORMAL_JOB.equals(backUpJobVO.getJobType())){
            backUpJobVO.setJobState(JobConstant.RUNNING);
        }else if (CRON_JOB.equals(backUpJobVO.getJobType())){
            backUpJobVO.setJobState(JobConstant.NORMAL);
            if (!CronUtils.isValid(backUpJobVO.getCronExpression())){
                throw new BizException("-1","cron表达式有误");
            }
            backUpJobVO.setNextTime(CronUtils.getNextRunTime(backUpJobVO.getCronExpression(),1).get(0));
        }else {
            throw new BizException(String.format("未知的任务类型:%s",backUpJobVO.getJobType()));
        }
        BackUpJob backUpJob = BACK_UP_JOB_VO_TO.convert(backUpJobVO);
        backUpJobMapper.insert(backUpJob);
        if (NORMAL_JOB.equals(backUpJobVO.getJobType())){
            backUpJobPool.execute(() -> backUpJobHandle.runJob(backUpJob));
        }
        return true;
    }

    @Override
    public Boolean update(BackUpJobVO backUpJobVO) {
        backUpJobVO.setNextTime(CronUtils.getNextRunTime(backUpJobVO.getCronExpression(),1).get(0));
        return backUpJobMapper.updateById(BACK_UP_JOB_VO_TO.convert(backUpJobVO)) > 0;
    }

    @Override
    public List<BackUpJob> getLessNowDateJob() {
        return backUpJobMapper.getLessNowDateJob(new Date());
    }

    @Override
    public IPage<BackUpJobVO> list(Integer page, Integer size,BackUpJobVO backUpJobVO) {
        LambdaQueryWrapper<BackUpJob> queryWrapper = new LambdaQueryWrapper<>();
        if (backUpJobVO != null){
            if (backUpJobVO.getDataSourceId() != null){
                queryWrapper.eq(BackUpJob::getDataSourceId,backUpJobVO.getDataSourceId());
            }
            if (StrUtil.isNotEmpty(backUpJobVO.getJobName())){
                queryWrapper.like(BackUpJob::getJobName,backUpJobVO.getJobName());
            }
            if (backUpJobVO.getJobType() != null){
                queryWrapper.eq(BackUpJob::getJobType,backUpJobVO.getJobType());
            }
            if (StrUtil.isNotEmpty(backUpJobVO.getJobState())){
                queryWrapper.eq(BackUpJob::getJobState,backUpJobVO.getJobState());
            }
        }
        IPage<BackUpJobVO> convert = backUpJobMapper.selectPage(new Page<>(page, size), queryWrapper)
                .convert(BACK_UP_JOB_TO_VO::convert);
        for (BackUpJobVO record : convert.getRecords()) {
            Long dataSourceId = record.getDataSourceId();
            DataSource dataSource = dataSourceService.getById(dataSourceId);
            record.setDataSourceName(dataSource.getSourceName());
        }
        return convert;
    }

    @Override
    public List<BackUpJobRecordAtomVO> recordList(Long id) {
        List<BackUpJobRecordAtomVO> result = new ArrayList<>();
        LambdaQueryWrapper<BackUpJobRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BackUpJobRecord::getJobId,id);
        queryWrapper.orderByDesc(BackUpJobRecord::getId);
        List<BackUpJobRecord> backUpJobRecords = backUpJobRecordService.list(queryWrapper);
        for (BackUpJobRecord record : backUpJobRecords) {
            LambdaQueryWrapper<BackUpJobRecordAtom> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(BackUpJobRecordAtom::getRecordId,record.getId());
            List<BackUpJobRecordAtom> backUpJobRecordAtoms = backUpJobRecordAtomService.list(queryWrapper1);
            BackUpJobRecordAtomVO backUpJobRecordAtomVO = BackUpJobRecordAtomVO.builder().build();
            backUpJobRecordAtomVO.setId(record.getId());
            backUpJobRecordAtomVO.setJobResult(record.getJobResult());
            backUpJobRecordAtomVO.setJobMessage(record.getJobMessage());
            backUpJobRecordAtomVO.setJobStartTime(record.getStartTime());
            backUpJobRecordAtomVO.setJobEndTime(record.getEndTime());
            backUpJobRecordAtomVO.setAtomVOList(BeanUtil.copyToList(backUpJobRecordAtoms,BackUpJobAtomVO.class));
            result.add(backUpJobRecordAtomVO);
        }
        return result;
    }

    @Override
    public Boolean enable(Long id) {
        BackUpJob backUpJob = backUpJobMapper.selectById(id);
        backUpJob.setJobEnable(OFF.equals(backUpJob.getJobEnable()) ? ON : OFF);
        return backUpJobMapper.updateById(backUpJob) > 0;
    }

    @Override
    public Boolean delete(Long id) {
        return backUpJobMapper.deleteById(id) > 0;
    }

}
