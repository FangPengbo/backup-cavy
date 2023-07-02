package cc.focc.cavy.service.impl;

import cc.focc.cavy.constant.JobConstant;
import cc.focc.cavy.model.entity.BackUpJob;
import cc.focc.cavy.model.entity.BackUpJobRecord;
import cc.focc.cavy.model.entity.BackUpJobRecordAtom;
import cc.focc.cavy.service.*;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    DataSourceService dataSourceService;

    @Autowired
    BackUpJobService backUpJobService;

    @Autowired
    BackUpJobRecordService backUpJobRecordService;

    @Autowired
    BackUpJobRecordAtomService backUpJobRecordAtomService;

    @Override
    public Map<String, Object> dashboard() {
        long dataSourceSize = dataSourceService.count();
        LambdaQueryWrapper<BackUpJob> backUpJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        backUpJobLambdaQueryWrapper.eq(BackUpJob::getJobState, JobConstant.RUNNING);
        long runningJobSize = backUpJobService.count(backUpJobLambdaQueryWrapper);
        LongSummaryStatistics collect = backUpJobRecordAtomService.list().stream().collect(Collectors.summarizingLong(BackUpJobRecordAtom::getFileSize));
        long countFileSize = collect.getSum();
        List<String> dateList = new ArrayList<>();
        List<Long> optionsSuccess = new ArrayList<>();
        List<Long> optionsFail = new ArrayList<>();
        List<Long> options2Success = new ArrayList<>();
        List<Long> options2Fail = new ArrayList<>();
        String today = DateUtil.today();
        dateList.add(today);
        for (int i = 1; i < 7; i++) {
            DateTime dateTime = DateUtil.offsetDay(DateUtil.parseDate(today), -i);
            String format = DateUtil.format(dateTime, DatePattern.NORM_DATE_PATTERN);
            dateList.add(format);
        }
        Collections.reverse(dateList);
        for (String s : dateList) {
            List<BackUpJobRecord> res = backUpJobRecordService.selectByDate(s);
            if (CollectionUtil.isEmpty(res)){
                optionsSuccess.add(0L);
                optionsFail.add(0L);
                options2Success.add(100L);
                options2Fail.add(0L);
            }else {
                Map<String, List<BackUpJobRecord>> collect1 = res.stream().collect(Collectors.groupingBy(BackUpJobRecord::getJobResult));
                List<BackUpJobRecord> successList = collect1.get(JobConstant.SUCCESS);
                List<BackUpJobRecord> failList = collect1.get(JobConstant.FAIL);

                long successSize = 0;
                long failSize = 0;
                if (CollectionUtil.isNotEmpty(successList)){
                    successSize = successList.size();
                }
                if (CollectionUtil.isNotEmpty(failList)){
                    failSize = failList.size();
                }
                long size = successSize + failSize;
                optionsSuccess.add(successSize);
                optionsFail.add(failSize);
                options2Success.add(successSize/size * 100);
                options2Fail.add(failSize/size * 100);
            }
        }
        return Map.of("dateList",dateList
                ,"optionsSuccess",optionsSuccess
                ,"optionsFail",optionsFail
                ,"options2Success",options2Success
                ,"options2Fail",options2Fail
                ,"dataSourceSize",dataSourceSize
                ,"runningJobSize",runningJobSize
                ,"countFileSize",countFileSize);
    }

}
