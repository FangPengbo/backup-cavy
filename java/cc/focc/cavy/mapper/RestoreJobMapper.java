package cc.focc.cavy.mapper;


import cc.focc.cavy.model.entity.BackUpJob;
import cc.focc.cavy.model.entity.RestoreJob;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface RestoreJobMapper extends BaseMapper<RestoreJob> {


}
