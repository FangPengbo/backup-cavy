package cc.focc.cavy.util;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cc.focc.cavy.constant.BackUpConstant.*;

@Slf4j
public class MySqlUtil {
    private static final Pattern pattern = Pattern.compile("ERROR \\d+ \\([A-Z0-9]+\\)");

    public static String hasError(List<String> list) {
        for (String s : list) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return s;
            }
        }
        return null;
    }

    public static List<String> getAllDataBase(String host, Integer port, String user, String pwd, List<String> excludeDataBase) throws RuntimeException{
        String cmd = String.format(CMD_MYSQL_ALL_DATABASE,host,user,port,pwd);
        log.debug("getAllDataBase cmd:{}",cmd);
        List<String> allDataBase = ExecUtil.exec(cmd);
        String error = hasError(allDataBase);
        if (StrUtil.isNotEmpty(error)){
            throw new RuntimeException(error);
        }
        if (CollectionUtil.isNotEmpty(excludeDataBase)){
            allDataBase.removeAll(excludeDataBase);
        }
        return allDataBase.stream().filter(el -> !el.startsWith("mysql:")).toList();
    }


    public static List<String> getAllTable(String host, Integer port, String user, String pwd, String dataBase, List<String> excludeTable) throws RuntimeException{
        String cmd = String.format(CMD_MYSQL_ALL_TABLE,host,user,port,pwd,dataBase,dataBase);
        log.debug("getAllTable cmd:{}",cmd);
        List<String> allTable = ExecUtil.exec(cmd);
        String error = hasError(allTable);
        if (StrUtil.isNotEmpty(error)){
            throw new RuntimeException(error);
        }
        if (CollectionUtil.isNotEmpty(excludeTable)){
            allTable.removeAll(excludeTable);
        }
        return allTable.stream().filter(el -> !el.startsWith("mysql:")).toList();
    }



}
