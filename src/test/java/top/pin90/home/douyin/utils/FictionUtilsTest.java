package top.pin90.home.douyin.utils;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FictionUtilsTest {

    private String inTextPath;

    @BeforeAll
    public void before(){
        URL url = this.getClass().getResource("fiction/in.txt");
        inTextPath = url.getFile();
    }


    @SneakyThrows
    @Test
    public void testConvertToLine(){
        Path path = Paths.get(inTextPath);
        System.out.println(path);
        byte[] bytes = Files.readAllBytes(path);
        String s = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(s);
    }
}
