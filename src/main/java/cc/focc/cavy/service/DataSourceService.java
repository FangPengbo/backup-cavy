package cc.focc.cavy.service;


import cc.focc.cavy.entity.table.DataSource;
import cc.focc.cavy.entity.vo.DataSourceVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;

public interface DataSourceService extends IService<DataSource> {

    boolean save (DataSourceVO dataSourceVO);

    IPage<DataSourceVO> listPage(Integer page,Integer size);
}
