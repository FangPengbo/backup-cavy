package cc.focc.cavy.service;


import cc.focc.cavy.model.entity.BackUpJob;
import cc.focc.cavy.model.entity.DataSource;
import cc.focc.cavy.model.vo.BackUpJobRecordAtomVO;
import cc.focc.cavy.model.vo.BackUpJobVO;
import cc.focc.cavy.model.vo.DataSourceVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BackUpJobService extends IService<BackUpJob> {

    Boolean save(BackUpJobVO backUpJobVO);

    Boolean update(BackUpJobVO backUpJobVO);

    List<BackUpJob> getLessNowDateJob();

    IPage<BackUpJobVO> list(Integer page, Integer size,BackUpJobVO backUpJobVO);

    List<BackUpJobRecordAtomVO> recordList(Long id);

    Boolean enable(Long id);

    Boolean delete(Long id);
}
