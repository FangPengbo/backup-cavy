package cc.focc.cavy.dispatch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SyncTask {

    @Value("${task.enable.sync}")
    private boolean enable;

    @Scheduled(cron = "${task.cron.sync}")
    public void execute(){
        if (!enable) return;
        System.out.println("SyncTask execute...");
    }


}
