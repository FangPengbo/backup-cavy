package cc.focc.cavy.util;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

public class StringUtil {

    public static void infoLog(StringBuilder builder,String content){
        builder.append("INFO:").append("\t").append(DateUtil.formatDateTime(new Date())).append("-").append(content).append("\n");
    }
    public static void errLog(StringBuilder builder,String content){
        builder.append("ERROR:").append("\t").append(DateUtil.formatDateTime(new Date())).append("-").append(content).append("\n");
    }

}
