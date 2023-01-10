package top.pin90.home.utils.douyin;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class FictionUtils {

    public final List<String> replaceToEmptySymbol = Arrays.asList("。", "【", "】", "」", "「");

    public final List<String> splitNewLineSymbol = Arrays.asList("，", ",");

    public final Pattern numberLinePattern = Pattern.compile("^\\s*\\d+\\s*、?\\s*(\\S*)");

    private final RestTemplate restTemplate;

    public FictionUtils(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<String> readlineAndRemoveEmptyLine(String paragraph) {
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


    public List<String> readlineFromUrl(String url) {
        return readlineFromUrl(url, null);
    }

    public List<String> readlineFromUrl(String url, Integer lineLimit) {
        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
        if (!entity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("http code error " + entity.getStatusCode().value());
        }
        if (!entity.hasBody()) {
            throw new RuntimeException("http body empty");
        }
        String html = entity.getBody();
        Document doc = Jsoup.parse(html);

        Element div = doc.getElementById("manuscript");
        if (div == null) {
            throw new RuntimeException("解析失败：找不到 manuscript");
        }

        Elements children = div.children();
        ArrayList<String> list = new ArrayList<>(children.size());
        int count = 0;
        for (Element p : children) {
            if (lineLimit != null && count >= lineLimit) {
                break;
            }
            list.add(p.text());
            count++;
        }
        return list;
    }

    public List<String> resolveLineList(List<String> lines, Function<String, String> sensitiveWordFunction, boolean saveNumberLineText) {
        return this.resolveLineList(lines, sensitiveWordFunction, saveNumberLineText, null);
    }

    /**
     * @param lines 要处理的行
     * @return 处理后的新行
     */
    public List<String> resolveLineList(List<String> lines, Function<String, String> sensitiveWordFunction, boolean saveNumberLineText, Integer lineLimit) {
        LinkedList<String> resultList = new LinkedList<>();

        for (String line : lines) {
            if (lineLimit != null && lineLimit <= resultList.size()) {
                log.info("lineLimit {} return", lineLimit);
                return resultList;
            }
            if (StringUtils.isBlank(line))
                continue;
            Pair<Boolean, String> detectNumberLine = detectNumberLine(line);
            // 是数字行
            if (detectNumberLine.getLeft()) {
                // 需要保留文本才执行
                if (saveNumberLineText) {
                    String t = detectNumberLine.getRight();
                    // 有需要的值才保留，其他时候直接删了
                    if (StringUtils.isNotBlank(t)) {
                        addToResultList(t, sensitiveWordFunction, resultList, lineLimit);
                    }
                }
            } else {
                addToResultList(line, sensitiveWordFunction, resultList, lineLimit);
            }
        }
        return resultList;
    }


    private void addToResultList(String text, Function<String, String> sensitiveWordFunction, List<String> resultList, Integer lineLimit) {
        String noSymbol = this.replaceSymbol(text);
        String noSensitive = sensitiveWordFunction.apply(noSymbol);
        Pair<Boolean, List<String>> detectSplit = detectSplit(noSensitive);
        if (detectSplit.getLeft()) {
            for (String s : detectSplit.getRight()) {
                if(StringUtils.isNotBlank(s)) {
                    resultList.add(s);
                    if (lineLimit != null && lineLimit <= resultList.size()) {
                        log.info("lineLimit {} return", lineLimit);
                        return;
                    }
                }
            }
        } else {
            resultList.add(noSensitive);
        }
    }

    public Pair<Boolean, List<String>> detectSplit(String text) {
        boolean requireSplit = false;
        ArrayList<String> list = new ArrayList<>(3);
        int i = 0;
        int j = 0;
        boolean[] flags = new boolean[splitNewLineSymbol.size()];
        int countNo = 0;
        outer:
        while (i < text.length()) {
            for (int l = 0; l < splitNewLineSymbol.size(); l++) {
                if (countNo == splitNewLineSymbol.size()) {
                    break outer;
                }
                if (!flags[l]) {
                    String symbol = splitNewLineSymbol.get(l);
                    int k = text.indexOf(symbol, i);
                    if (k != -1) {
                        requireSplit = true;
                        String t = text.substring(i, k);
                        if(StringUtils.isNotBlank(t)) {
                            list.add(t);
                        }
                        j = k + 1;
                        i = j;
//                        log.debug("text= {}, i= {}, j= {}, k= {}\n", text, i, j, k);
                        continue;
                    } else {
                        flags[l] = true;
                        countNo++;
                    }
                }
            }
        }
        if (j > 0) {
            String t = text.substring(j);
            if(StringUtils.isNotBlank(t)) {
                list.add(t);
            }
        }
        if (!requireSplit) {
            list.add(text);
        }
        return ImmutablePair.of(requireSplit, list);
    }

    public Pair<Boolean, String> detectNumberLine(String line) {
        Matcher matcher = numberLinePattern.matcher(line);
        if (matcher.matches()) {
            String group = matcher.group(1);
            if (StringUtils.isNotBlank(group)) {
                return ImmutablePair.of(true, group);
            } else {
                return ImmutablePair.of(true, null);
            }
        }
        return ImmutablePair.of(false, null);
    }

    public String replaceSymbol(String paragraph) {
        for (String s : replaceToEmptySymbol) {
            paragraph = paragraph.replace(s, "");
        }
        return paragraph;
    }

}
