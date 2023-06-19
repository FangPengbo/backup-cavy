package cc.focc.cavy.util;

import cn.hutool.core.util.RuntimeUtil;

import java.util.List;

public class ExecUtil {

    public static List<String> exec(String cmd){
        return RuntimeUtil.execForLines("/bin/sh","-c",cmd);
    }

}
