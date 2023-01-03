package top.pin90.home.douyin.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.StringReader;
import java.util.*;

public class FictionUtils {

    public static List<String> replaceSensitiveWordToPinyin(List<String> paragraph) {
        for (int i = 0; i < paragraph.size(); i++) {
            String line = replaceSensitiveWordToPinyin(paragraph.get(i));
            paragraph.set(i, line);
        }
        return paragraph;
    }


    public static String replaceSensitiveWordToPinyin(String line) {
        return line;
    }


    public static String replaceSymbol(String paragraph) {
        return paragraph;
//                .replace("\n", "")
//                .replace("\r", "");
    }

    public static List<String> readlineAndRemoveEmptyLine(String paragraph) {
        StringReader reader = new StringReader(paragraph);
        Scanner scan = new Scanner(reader).useDelimiter("\n+");
        LinkedList<String> result = new LinkedList<>();
        while (scan.hasNext()) {
            String next = scan.next();
            if (StringUtils.isNotBlank(next)) {
                result.add(next);
            }
        }
        return result;
    }

}
