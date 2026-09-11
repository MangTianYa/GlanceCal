# 日历

使用 Kotlin 和 Jetpack Compose 开发的 Material 3 中国日历 Android 应用。

## 功能

- 阳历月视图、星期与日期详情
- 基于 `6tail/lunar-java` 的农历、节气、传统节日和每日黄历宜忌
- 基于 `holiday-cn` 的法定节假日、休息日及调休上班标记
- 节假日数据本地缓存，农历和黄历支持完全离线使用
- Material You 动态配色、系统深色模式和手机/平板响应式布局
- 支持左右滑动切换月份，并提供流畅的页面切换动画

## 构建

项目需要 JDK 17 或更高版本、Android SDK 35。使用 Android Studio 打开项目后运行 `app`，或执行：

```shell
./gradlew assembleDebug
```

应用首次查看某年份时会从 jsDelivr 获取 `holiday-cn` 数据。点击顶部刷新按钮可更新当前年份附近的缓存。

## 数据来源

- [6tail/lunar-java](https://github.com/6tail/lunar-java)，MIT License
- [NateScarlet/holiday-cn](https://github.com/NateScarlet/holiday-cn)，MIT License
