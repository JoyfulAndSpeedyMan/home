package top.pin90.home.common.utils.file;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

public class ClasspathUtils {

    public static String classpathBase ;
    static {
        URL resource = ClasspathUtils.class.getResource("/");
        try {
            File dir = ResourceUtils.getFile(resource, "/");
            classpathBase = dir.getAbsolutePath();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    public static File getFile(String classpath) throws FileNotFoundException {
        return new File(classpathBase + File.separator + classpath);
    }


}
