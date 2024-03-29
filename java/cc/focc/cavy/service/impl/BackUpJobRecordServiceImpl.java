package cc.focc.cavy.service.impl;

import cc.focc.cavy.mapper.BackUpJobRecordMapper;
import cc.focc.cavy.model.entity.BackUpJobRecord;
import cc.focc.cavy.service.BackUpJobRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class BackUpJobRecordServiceImpl extends ServiceImpl<BackUpJobRecordMapper, BackUpJobRecord> implements BackUpJobRecordService {

    @Autowired
    BackUpJobRecordMapper mapper;

    @Override
    public List<BackUpJobRecord> selectByDate(String date) {
        return mapper.selectByDate(date);
    }
}
