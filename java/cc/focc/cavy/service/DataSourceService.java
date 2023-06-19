package cc.focc.cavy.service;


import cc.focc.cavy.model.entity.DataSource;
import cc.focc.cavy.model.vo.DataSourceVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DataSourceService extends IService<DataSource> {

    boolean save (DataSourceVO dataSourceVO);

    IPage<DataSourceVO> listPage(Integer page, Integer size);

    DataSource selectOneByPrimary(Long dataSourceId);
}
