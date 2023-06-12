package cc.focc.cavy.dispatch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackUpTask {

    @Value("${task.enable.backup}")
    private boolean enable;

    @Scheduled(cron = "${task.cron.backup}")
    public void execute(){
        if (!enable) return;
        System.out.println("BackUpTask execute...");
    }


}
