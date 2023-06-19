package cc.focc.cavy.util;


import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CronUtils {

    public static boolean isValid(String cron){
        try {
            new CronExpression(cron);
        }catch (Exception e){
            return false;
        }
        return true;
    }


    public static List<Date> getNextRunTime(String cronExpression,int numTimes){
        List<Date> result = new ArrayList<>();
        CronExpression cron;
        try {
            cron = new CronExpression(cronExpression);
        } catch (ParseException e) {
            return result;
        }
        Date now = new Date();
        for (int i = 0; i < numTimes; i++) {
            Date next = cron.getNextValidTimeAfter(now);
            result.add(next);
            now = next;
        }
        return result;
    }


}
