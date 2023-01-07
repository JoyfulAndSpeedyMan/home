package top.pin90.home.common.utils;

import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import com.github.houbb.sensitive.word.api.ISensitiveWordReplace;
import com.github.houbb.sensitive.word.api.ISensitiveWordReplaceContext;
import com.github.houbb.sensitive.word.api.IWordDeny;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;

import java.util.Arrays;
import java.util.List;

/**
 * 关键词脱敏
 */
public class SensitiveWordUtils {

    public static final ISensitiveWordReplace REPLACE_PIN_YIN = new SensitiveWordReplacePinyin();

    public static final IWordDeny MY_WORD_DENY = new MyWordDeny();

    public static final SensitiveWordBs sensitiveWordBs = SensitiveWordBs.newInstance()
            .wordDeny(MY_WORD_DENY)
            .init();

    public static String replaceSensitiveWordToPinyin(String s) {
        return sensitiveWordBs.replace(s, REPLACE_PIN_YIN);
    }


    public static class SensitiveWordReplacePinyin implements ISensitiveWordReplace {

        @Override
        public String replace(ISensitiveWordReplaceContext context) {
            String sensitiveWord = context.sensitiveWord();
            return PinyinHelper.toPinyin(sensitiveWord, PinyinStyleEnum.DEFAULT);
        }
    }

    public static class MyWordDeny implements IWordDeny {

        public static List<String> denyWords = Arrays.asList("死", "杀", "尸", "国", "血", "警", "药", "毒", "鬼");

        @Override
        public List<String> deny() {
            return denyWords;
        }

    }
}