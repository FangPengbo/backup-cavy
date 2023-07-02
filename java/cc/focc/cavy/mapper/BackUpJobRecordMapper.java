package cc.focc.cavy.mapper;


import cc.focc.cavy.model.entity.BackUpJob;
import cc.focc.cavy.model.entity.BackUpJobRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BackUpJobRecordMapper extends BaseMapper<BackUpJobRecord> {

    List<BackUpJobRecord> selectByDate(String date);

}
