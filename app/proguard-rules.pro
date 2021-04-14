# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5
# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers
# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify
# 忽略警告
-ignorewarnings
# 避免混淆泛型
-keepattributes Signature
# 项目混淆后产生映射文件 包含有类名->混淆后类名的映射关系
-verbose
# 指定混淆是采用的算法，参数是一个过滤器(谷歌推荐的算法，一般不做更改)
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# 不优化输入的类文件
#-dontoptimize

-dontwarn

# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService


# 保留R下面的资源
-keep class **.R$* {*;}
-keep public class com.sdt.testthreeso.R$*{
    public static final int *;
}

-dontwarn android.view.**
# 保护注解 内部类
-keepattributes *Annotation*, InnerClasses
-keepattributes *JavascriptInterface*

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持Activity中View及其子类入参的方法
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# 保持所有拥有本地方法的类名及本地方法名
-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class android.support.v8.renderscript.** { *; }

-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class * extends android.widget.TextView {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View {
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

-keep interface android.content.pm.** { *; }
-keep class android.content.pm.** { *; }

-keep public class * extends org.apache.mina.**
-keep class org.apache.** { *; }
-dontnote org.apache.http.**
-dontnote android.net.http.**

-assumenosideeffects class android.util.Log {
     public static *** v(...);
    #public static *** d(...);
    #public static *** i(...);
}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keep class com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-keep class org.**{ *; }
-dontwarn org.**


-keep class java.** { *; }
-dontwarn java.**

-keep class javax.** { *; }
-dontwarn javax.**

-keep class com.google.code.microlog4android.**
-dontwarn com.google.code.microlog4android.**

-keep class okio.** { *; }
-dontwarn okio.**

-keep class com.google.android.**{ *; }
-dontwarn com.google.android.**

# fresco
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

-keep interface com.facebook.fresco.** { *; }
-keep enum com.facebook.fresco.** { *; }
-keep class com.facebook.** { *; }
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.facebook.infer.**

# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**

# Retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn javax.annotation.**
-dontwarn javax.inject.**

# RxJava RxAndroid
-keep class io.reactivex.**{*;}
-keepclasseswithmembernames class io.reactivex{*;}
-dontwarn io.reactivex.**
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

