package cc.focc.cavy.util;

import java.io.File;

public class FileUtil {

    public static Long computeFileSize (String filePath){
        File file = new File(filePath);
        if (file.isDirectory()){
            return 0L;
        }
        return file.length();
    }


    public static void deleteAll(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    deleteAll(subFile.getAbsolutePath());
                    subFile.delete();
                }
            }
            file.delete();
        }
    }
}
