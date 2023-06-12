package cc.focc.cavy.convert;


import cc.focc.cavy.model.entity.DataSource;
import cc.focc.cavy.model.vo.DataSourceVO;

import java.util.List;

public class DataSourceToDataSourceVO extends AbstractConvert<DataSource, DataSourceVO>{

    @Override
    public List<DataSourceVO> convert(List<DataSource> dataSources) {
        return super.convert(dataSources);
    }

    @Override
    public DataSourceVO convert(DataSource t) {
        DataSourceVO v = new DataSourceVO();
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
