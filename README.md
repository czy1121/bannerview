# BannerView
 
简单好用的水平轮播控件


## 用法

```kotlin

banner.indicator = RectangleIndicator(this)

// 安装适配器，绑定生命周期，开始轮播
banner.setup(adapter).bind(this).start() 
```

## Gradle

``` groovy
repositories {
    maven { url "https://gitee.com/ezy/repo/raw/cosmo/"}
}
dependencies {
    implementation "me.reezy.cosmo:bannerview:0.8.0"
}
```

## LICENSE

The Component is open-sourced software licensed under the [Apache license](LICENSE).