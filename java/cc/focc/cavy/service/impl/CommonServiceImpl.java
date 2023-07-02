package cc.focc.cavy.service.impl;

import cc.focc.cavy.model.entity.DataSource;
import cc.focc.cavy.service.CommonService;
import cc.focc.cavy.service.DataSourceService;
import cc.focc.cavy.util.AESUtil;
import cc.focc.cavy.util.CronUtils;
import cc.focc.cavy.util.MySqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    AESUtil aesUtil;

    @Autowired
    DataSourceService dataSourceService;


    @Override
    public List<String> getAllDataBaseByDataSource(Long dataSourceId) {
        DataSource dataSource = dataSourceService.getById(dataSourceId);
        return MySqlUtil.getAllDataBase(dataSource.getSourceHost(),dataSource.getSourcePort(),dataSource.getSourceUser(),aesUtil.decrypt(dataSource.getSourcePwd()),null);
    }

    @Override
    public List<Date> lastRunTime(String cron) {
        return CronUtils.getNextRunTime(cron, 5);
    }
}
