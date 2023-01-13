package top.pin90.home.utils.douyin.record.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class WechatRecordGenerateConfigV2 {

    private ThemeConfig themeConfig;

    private DataConfig dataConfig;

    private DrawConfig drawConfig;

}
