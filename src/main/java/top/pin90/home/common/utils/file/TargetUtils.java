package top.pin90.home.common.utils.file;

import java.io.File;

public class TargetUtils {

    private final static String TARGET_DIR = "target";

    private static File getFile(String path){
        return new File(TARGET_DIR + File.separator + path);
    }
}
