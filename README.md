# 日历

[![Android CI](https://github.com/MangTianYa/GlanceCal/actions/workflows/android.yml/badge.svg)](https://github.com/MangTianYa/GlanceCal/actions/workflows/android.yml)
[![Release](https://img.shields.io/github/v/release/MangTianYa/GlanceCal?display_name=tag)](https://github.com/MangTianYa/GlanceCal/releases/latest)

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

## 自动构建与发布

推送到 `main` 或创建 Pull Request 时，GitHub Actions 会运行单元测试、构建 Debug APK，并将 APK 上传到该次工作流的 Artifacts。

发布签名 APK 前，在仓库 `Settings > Secrets and variables > Actions` 中配置以下 Secrets：

- `SIGNING_KEYSTORE_BASE64`：`calendar-release.jks` 的 Base64 内容
- `SIGNING_STORE_PASSWORD`：密钥库密码
- `SIGNING_KEY_ALIAS`：密钥别名
- `SIGNING_KEY_PASSWORD`：密钥密码

在 PowerShell 中可通过以下命令生成 Base64 内容：

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("calendar-release.jks"))
```

配置完成后，推送符合 `v*` 格式的标签即可自动构建签名 APK并发布到 GitHub Releases：

```shell
git tag v1.0.0
git push origin v1.0.0
```

也可以从 GitHub Actions 的 `Publish Release` 页面手动运行工作流，输入 `1.0.0` 或 `v1.0.0`。工作流会从所选分支构建并创建对应标签。不要输入 `*`。Release 页面会包含 APK 和对应的 SHA-256 校验文件。

## 数据来源

- [6tail/lunar-java](https://github.com/6tail/lunar-java)，MIT License
- [NateScarlet/holiday-cn](https://github.com/NateScarlet/holiday-cn)，MIT License
