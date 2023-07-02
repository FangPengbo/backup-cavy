package cc.focc.cavy.service;


import cc.focc.cavy.model.entity.BackUpJobRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BackUpJobRecordService extends IService<BackUpJobRecord> {


    List<BackUpJobRecord> selectByDate(String date);

}
