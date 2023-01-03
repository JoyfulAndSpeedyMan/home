package top.pin90.home.douyin.utils;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.pin90.home.common.utils.file.ClasspathUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FictionUtilsTest {

    private String inTextPath;

    @SneakyThrows
    @BeforeEach
    public void before() {
        File file = ClasspathUtils.getFile("/douyin/utils/fiction/in.txt");
        inTextPath = file.getAbsolutePath();
    }


    @SneakyThrows
    @Test
    public void testConvertToLine() {
        Path path = Paths.get(inTextPath);
        System.out.println(path);
        byte[] bytes = Files.readAllBytes(path);
        String s = new String(bytes, StandardCharsets.UTF_8);
        s = FictionUtils.replaceSymbol(s);
        System.out.println(s);
//        List<String> lines = FictionUtils.readlineAndRemoveEmptyLine(s);
//        System.out.println(lines);
//        FictionUtils.replaceSensitiveWordToPinyin(lines);
    }
}
