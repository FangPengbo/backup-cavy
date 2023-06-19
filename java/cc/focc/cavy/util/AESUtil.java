package cc.focc.cavy.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class AESUtil {

    public static String commonAesKey;

    @Value("${def.aes-key}")
    public void setCommonAesKey(String commonAesKey) {
        AESUtil.commonAesKey = commonAesKey;
    }

    public static String encrypt(String content){
        AES aes = SecureUtil.aes(commonAesKey.getBytes(StandardCharsets.UTF_8));
        return aes.encryptHex(content);
    }

    public static String decrypt(String content){
        AES aes = SecureUtil.aes(commonAesKey.getBytes(StandardCharsets.UTF_8));
        return aes.decryptStr(content);
    }

}
