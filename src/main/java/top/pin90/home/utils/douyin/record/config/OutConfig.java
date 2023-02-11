package top.pin90.home.utils.douyin.record.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
@ToString
public class OutConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 7525419414829921115L;

    public static final String DEFAULT_OUT_FILE = "result.png";

    public static final String DEFAULT_MID_IMG_OUT_DIR = "mid";

    public static final String DEFAULT_SECTION_OUT_DIR = "section";


    private String baseOutDir;

    private boolean outResult = true;

    private String resultFile;

    private boolean outMidImg = true;

    private String outMidImgDir;

    private boolean sectionOut = true;

    private String sectionOutDir;

    private int pageSize = 100;

    private List<Section> sectionList;

    public SectionWriterControl sectionWriter(SectionWriter run) {
        return new SectionWriterControl(run, sectionIter());
    }

    public SectionIter sectionIter() {
        return new SectionIter();
    }

    public class SectionWriterControl implements Serializable {

        @Serial
        private static final long serialVersionUID = -4509723220147049246L;

        private final SectionWriter run;

        private final SectionIter sectionIter;

        private Section curSection;

        private int startIndex = -1;

        private int curIndex = 1;

        private int sectionNum = 1;

        public SectionWriterControl(SectionWriter run, SectionIter sectionIter) {
            Assert.isTrue(sectionIter.hasNext(), "sectionIter 没有元素");
            this.run = run;
            this.sectionIter = sectionIter;
            this.curSection = sectionIter.next();
            this.startIndex = this.curSection.start;
        }

        public void incr() {
            if (curSection != null && curSection.end != null && curSection.end == curIndex) {
                run.write(curIndex, sectionNum++, curSection);
                startIndex = curIndex + 1;
                curSection = sectionIter.hasNext() ? sectionIter.next() : null;
            }
            curIndex++;
        }

        public void afterRun() {
            if (curSection != null) {
                int start = Math.max(startIndex, curSection.start);
                int end = curSection.end != null && curSection.end < curIndex - 1 ? curSection.end : curIndex - 1;
                if (end - start >= 1) {
                    run.write(curIndex - 1, sectionNum, curSection);
                }
            }
        }
    }

    public static interface SectionWriter {
        void write(Integer curIndex, Integer sectionIndex, OutConfig.Section section);
    }

    public class SectionIter implements Serializable, Iterator<Section> {

        @Serial
        private static final long serialVersionUID = 4742631842309673887L;

        private boolean noUseSectionList = CollectionUtils.isEmpty(sectionList);
        private Iterator<Section> listIter = noUseSectionList ? null : sectionList.iterator();

        private int curPage = 0;

        @Override
        public boolean hasNext() {
            if (noUseSectionList) {
                return true;
            } else {
                return listIter.hasNext();
            }
        }

        @Override
        public Section next() {
            if (noUseSectionList) {
                return new Section(curPage++ * pageSize, curPage * pageSize - 1);
            } else {
                return listIter.next();
            }
        }
    }

    public record Section(int start, Integer end) implements Serializable {

        @Serial
        private static final long serialVersionUID = -8268352805900710236L;

        public boolean contain(int i) {
            return i >= start && i <= end;
        }

    }

}

