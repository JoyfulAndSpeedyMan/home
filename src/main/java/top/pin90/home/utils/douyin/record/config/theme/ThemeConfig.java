package top.pin90.home.utils.douyin.record.config.theme;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.awt.*;

@Getter
@Setter
@ToString
public class ThemeConfig {

    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public final static ThemeConfig DEFAULT_CONFIG = new ImmutableDelegateThemeConfig(defaultConfig());

    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public final static ThemeConfig DARK_CONFIG = new ImmutableDelegateThemeConfig(darkConfig());


    private Color backgroundColor = new Color(237, 237, 237);

    private Color meBoxColor = new Color(169, 234, 122);

    private Color meTextColor = new Color(16, 23, 11);

    private Color otherBoxColor = new Color(255, 255, 255);

    private Color otherTextColor = new Color(26, 26, 26);

    private Color timeLineBoxColor = new Color(255, 255, 255, 10);

    private Color timeLineTextColor = new Color(88, 88, 88);

    private Color slotColor = new Color(211, 211, 211);

    ThemeConfig() {}

    public static ThemeConfig defaultConfig() {
        ThemeConfig defaultConfig = new ThemeConfig();
        defaultConfig.setBackgroundColor(new Color(237, 237, 237));
        defaultConfig.setMeBoxColor(new Color(169, 234, 122));
        defaultConfig.setMeTextColor(new Color(16, 23, 11));
        defaultConfig.setOtherBoxColor(new Color(255, 255, 255));
        defaultConfig.setOtherTextColor(new Color(26, 26, 26));
        defaultConfig.setTimeLineBoxColor(new Color(255, 255, 255, 0));
        defaultConfig.setTimeLineTextColor(new Color(88, 88, 88));
        defaultConfig.setSlotColor(new Color(211, 211, 211));
        return defaultConfig;
    }

    public static ThemeConfig darkConfig() {
        ThemeConfig darkTheme = new ThemeConfig();
        darkTheme.setBackgroundColor(new Color(17, 17, 17));
        darkTheme.setMeBoxColor(new Color(89, 178, 105));
        darkTheme.setMeTextColor(new Color(6, 18, 10));
        darkTheme.setOtherBoxColor(new Color(44, 44, 44));
        darkTheme.setOtherTextColor(new Color(213, 213, 213));
        darkTheme.setTimeLineBoxColor(new Color(255, 255, 255, 0));
        darkTheme.setTimeLineTextColor(new Color(86, 86, 86));
        darkTheme.setSlotColor(new Color(211, 211, 211));
        return darkTheme;
    }
}
