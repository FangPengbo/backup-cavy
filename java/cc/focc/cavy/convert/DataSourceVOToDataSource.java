package cc.focc.cavy.convert;


import cc.focc.cavy.model.entity.DataSource;
import cc.focc.cavy.model.vo.DataSourceVO;

import java.util.List;

public class DataSourceVOToDataSource extends AbstractConvert<DataSourceVO, DataSource>{
    @Override
    public List<DataSource> convert(List<DataSourceVO> dataSourceVOS) {
        return super.convert(dataSourceVOS);
    }

    @Override
    public DataSource convert(DataSourceVO t) {
        DataSource v = new DataSource();
        v.setId(t.getId());
        v.setSourceName(t.getSourceName());
        v.setSourceType(t.getSourceType());
        v.setSourceHost(t.getSourceHost());
        v.setSourcePort(t.getSourcePort());
        v.setSourceUser(t.getSourceUser());
        v.setSourcePwd(t.getSourcePwd());
        return v;
    }

}
