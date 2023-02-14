package top.pin90.home.utils.douyin.record.config.theme;

import java.awt.*;

public class ImmutableDelegateThemeConfig extends ImmutableThemeConfig {

    private ThemeConfig themeConfig;

    public ImmutableDelegateThemeConfig(ThemeConfig themeConfig) {
        this.themeConfig = themeConfig;
    }

    @Override
    public Color getBackgroundColor() {
        return themeConfig.getBackgroundColor();
    }

    @Override
    public Color getMeBoxColor() {
        return themeConfig.getMeBoxColor();
    }

    @Override
    public Color getMeTextColor() {
        return themeConfig.getMeTextColor();
    }

    @Override
    public Color getOtherBoxColor() {
        return themeConfig.getOtherBoxColor();
    }

    @Override
    public Color getOtherTextColor() {
        return themeConfig.getOtherTextColor();
    }

    @Override
    public Color getTimeLineBoxColor() {
        return themeConfig.getTimeLineBoxColor();
    }

    @Override
    public Color getTimeLineTextColor() {
        return themeConfig.getTimeLineTextColor();
    }

    @Override
    public Color getSlotColor() {
        return themeConfig.getSlotColor();
    }

    @Override
    public String toString() {
        return themeConfig.toString();
    }
}
