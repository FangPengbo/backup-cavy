package cc.focc.cavy.service;

import cc.focc.cavy.model.entity.RestoreJob;
import cc.focc.cavy.model.vo.RestoreJobRecordVO;
import cc.focc.cavy.model.vo.RestoreJobVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RestoreJobService extends IService<RestoreJob> {

    IPage<RestoreJobVO> list(Integer page, Integer size, RestoreJobVO restoreJobVO);

    List<RestoreJobRecordVO> recordList(Long id);
}
