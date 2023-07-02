package cc.focc.cavy.service.impl;

import cc.focc.cavy.convert.RestoreJobRecordToVO;
import cc.focc.cavy.convert.RestoreJobToVO;
import cc.focc.cavy.mapper.RestoreJobMapper;
import cc.focc.cavy.mapper.RestoreJobRecordMapper;
import cc.focc.cavy.model.entity.*;
import cc.focc.cavy.model.vo.RestoreJobRecordVO;
import cc.focc.cavy.model.vo.RestoreJobVO;
import cc.focc.cavy.service.DataSourceService;
import cc.focc.cavy.service.RestoreJobService;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestoreJobServiceImpl extends ServiceImpl<RestoreJobMapper, RestoreJob> implements RestoreJobService {

    public static final RestoreJobToVO convertVO = new RestoreJobToVO();
    public static final RestoreJobRecordToVO restoreJobRecordToVO = new RestoreJobRecordToVO();

    @Autowired
    RestoreJobMapper restoreJobMapper;

    @Autowired
    DataSourceService dataSourceService;

    @Autowired
    RestoreJobRecordMapper restoreJobRecordMapper;

    @Override
    public IPage<RestoreJobVO> list(Integer page, Integer size, RestoreJobVO restoreJobVO) {
        LambdaQueryWrapper<RestoreJob> queryWrapper = new LambdaQueryWrapper<>();
        if (restoreJobVO != null){
            if (restoreJobVO.getDataSourceId() != null){
                queryWrapper.eq(RestoreJob::getDataSourceId,restoreJobVO.getDataSourceId());
            }
            if (StrUtil.isNotEmpty(restoreJobVO.getJobState())){
                queryWrapper.eq(RestoreJob::getJobState,restoreJobVO.getJobState());
            }
        }
        IPage<RestoreJobVO> convert = restoreJobMapper.selectPage(new Page<>(page, size), queryWrapper)
                .convert(convertVO::convert);
        for (RestoreJobVO record : convert.getRecords()) {
            Long dataSourceId = record.getDataSourceId();
            DataSource dataSource = dataSourceService.getById(dataSourceId);
            record.setDataSourceName(dataSource.getSourceName());
        }
        return convert;
    }

    @Override
    public List<RestoreJobRecordVO> recordList(Long id) {
        LambdaQueryWrapper<RestoreJobRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RestoreJobRecord::getRestoreJobId,id);
        List<RestoreJobRecord> restoreJobRecords = restoreJobRecordMapper.selectList(queryWrapper);
        return restoreJobRecordToVO.convert(restoreJobRecords);
    }

}
