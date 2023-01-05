package top.pin90.home.douyin.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.pin90.home.common.utils.file.ClasspathUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FictionUtilsTest {

    private String inTextPath;

    @BeforeEach
    public void before() throws IOException {
        File file = ClasspathUtils.getFile("/douyin/utils/fiction/in.txt");
        inTextPath = file.getAbsolutePath();
    }

    @Test
    public void testConvertToLine() throws IOException {
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
