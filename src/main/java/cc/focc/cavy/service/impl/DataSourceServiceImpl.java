package cc.focc.cavy.service.impl;

import cc.focc.cavy.convert.DataSourceToDataSourceVO;
import cc.focc.cavy.convert.DataSourceVOToDataSource;
import cc.focc.cavy.entity.table.DataSource;
import cc.focc.cavy.entity.vo.DataSourceVO;
import cc.focc.cavy.mapper.DataSourceMapper;
import cc.focc.cavy.service.DataSourceService;
import cc.focc.cavy.util.AESUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource> implements DataSourceService {
    public static final DataSourceVOToDataSource dataSourceVOToDataSource = new DataSourceVOToDataSource();
    public static final DataSourceToDataSourceVO dataSourceToDataSourceVO = new DataSourceToDataSourceVO();

    @Autowired
    private AESUtil aesUtil;

    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Override
    public boolean save(DataSourceVO dataSourceVO) {
        dataSourceVO.setSourcePwd(aesUtil.encrypt(dataSourceVO.getSourcePwd()));
        return this.save(dataSourceVOToDataSource.convert(dataSourceVO));
    }

    @Override
    public IPage<DataSourceVO> listPage(Integer page, Integer size) {
        return dataSourceMapper.selectPage(new Page<>(page, size), null).convert(el -> {
            DataSourceVO convert = dataSourceToDataSourceVO.convert(el);
            convert.setSourcePwd("******");
            return convert;
        });
    }


}
