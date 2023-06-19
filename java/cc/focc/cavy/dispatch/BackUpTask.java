package cc.focc.cavy.dispatch;

import cc.focc.cavy.core.BackUpJobHandle;
import cc.focc.cavy.model.entity.BackUpJob;
import cc.focc.cavy.service.BackUpJobService;
import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class BackUpTask {

    @Value("${task.enable.backup}")
    private boolean enable;

    @Resource(name = "backUpJobPool")
    private ThreadPoolExecutor backUpJobPool;

    @Autowired
    BackUpJobService jobService;

    @Autowired
    BackUpJobHandle jobHandle;

    @Scheduled(cron = "${task.cron.backup}")
    public void execute(){
        if (!enable) return;
        log.info("BackUpTask execute ...");
        List<BackUpJob> jobs = jobService.getLessNowDateJob();
        if (CollectionUtil.isEmpty(jobs)) return;
        for (BackUpJob job : jobs) backUpJobPool.execute(() -> jobHandle.runJob(job));
    }


}
