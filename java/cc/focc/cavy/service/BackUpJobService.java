package cc.focc.cavy.service;


import cc.focc.cavy.model.entity.BackUpJob;
import cc.focc.cavy.model.entity.DataSource;
import cc.focc.cavy.model.vo.BackUpJobVO;
import cc.focc.cavy.model.vo.DataSourceVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BackUpJobService extends IService<BackUpJob> {

    Boolean save(BackUpJobVO backUpJobVO);

    List<BackUpJob> getLessNowDateJob();

}
