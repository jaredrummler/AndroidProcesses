# AndroidProcesses
A small library to get the current running processes on Android
___

Why would I need this?
----------------------

As of Android 5.0, it has become increasingly difficult to get a list of running apps. [`getRunningTasks(int)`](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.html#getRunningTasks(int)) is now deprecated. Android 5.1.1+ killed [`getRunningAppProcesses()`](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.html#getRunningAppProcesses()) (as of Android 5.1.1+ it only returns your app). The documentation hasn't changed and Google is ignoring requests to either update the documentation or restore the original implementation. 

Using [UsageStatsManager](https://developer.android.com/reference/android/app/usage/UsageStatsManager.html), it is possible to get a list of running apps. However, this requires the user to grant your application special permissions in Settings. It has been reported that some OEMs have removed this setting.

This library gets a list of running apps and doesn't require any permissions. See the [sample](https://github.com/jaredrummler/AndroidProcesses/blob/master/sample/src/main/java/com/jaredrummler/android/processes/sample/MainActivity.java) application for details.

Download
--------

Download [the latest AAR](https://repo1.maven.org/maven2/com/jaredrummler/android-processes/1.0.0/android-processes-1.0.0.aar) or grab via Gradle:

```groovy
compile 'com.jaredrummler:android-processes:1.0.0'
```
or Maven:
```xml
<dependency>
  <groupId>com.jaredrummler</groupId>
  <artifactId>android-processes</artifactId>
  <version>1.0.0</version>
</dependency>
```

