package cc.focc.cavy.service;

import java.util.Date;
import java.util.List;

public interface CommonService {

    List<String> getAllDataBaseByDataSource(Long dataSourceId);

    List<Date> lastRunTime(String cron);
}
