package cc.focc.cavy.service.impl;

import cc.focc.cavy.convert.DataSourceToVO;
import cc.focc.cavy.convert.DataSourceVOTo;
import cc.focc.cavy.model.entity.DataSource;
import cc.focc.cavy.mapper.DataSourceMapper;
import cc.focc.cavy.model.vo.DataSourceVO;
import cc.focc.cavy.service.DataSourceService;
import cc.focc.cavy.util.AESUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource> implements DataSourceService {
    public static final DataSourceVOTo DATA_SOURCE_VO_TO = new DataSourceVOTo();
    public static final DataSourceToVO DATA_SOURCE_TO_VO = new DataSourceToVO();

    @Autowired
    private AESUtil aesUtil;

    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Override
    public boolean save(DataSourceVO dataSourceVO) {
        dataSourceVO.setSourcePwd(aesUtil.encrypt(dataSourceVO.getSourcePwd()));
        return this.save(DATA_SOURCE_VO_TO.convert(dataSourceVO));
    }

    @Override
    public IPage<DataSourceVO> listPage(Integer page, Integer size,DataSourceVO dataSourceVO) {
        LambdaQueryWrapper<DataSource> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(dataSourceVO.getSourceName())){
            queryWrapper.like(DataSource::getSourceName,dataSourceVO.getSourceName());
        }
        if (StrUtil.isNotBlank(dataSourceVO.getSourceHost())){
            queryWrapper.like(DataSource::getSourceHost,dataSourceVO.getSourceHost());
        }
        if (StrUtil.isNotBlank(dataSourceVO.getSourceType())){
            queryWrapper.eq(DataSource::getSourceType,dataSourceVO.getSourceType());
        }
        return dataSourceMapper.selectPage(new Page<>(page, size), queryWrapper)
                .convert(el -> {
            DataSourceVO convert = DATA_SOURCE_TO_VO.convert(el);
            convert.setSourcePwd("******");
            return convert;
        });
    }

    @Override
    public DataSource selectOneByPrimary(Long dataSourceId) {
        return dataSourceMapper.selectById(dataSourceId);
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(DataSourceVO dataSourceVO) {
        if (StrUtil.isEmpty(dataSourceVO.getSourcePwd()) || "******".equals(dataSourceVO.getSourcePwd())){
            dataSourceVO.setSourcePwd(null);
        }else {
            dataSourceVO.setSourcePwd(aesUtil.encrypt(dataSourceVO.getSourcePwd()));
        }
        return updateById(DATA_SOURCE_VO_TO.convert(dataSourceVO));
    }

    @Override
    public List<DataSource> all() {
        return list().stream().map(el ->{
            DataSource dataSource = new DataSource();
            dataSource.setId(el.getId());
            dataSource.setSourceName(el.getSourceName());
            return dataSource;
        }).collect(Collectors.toList());
    }


}
