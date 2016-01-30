# AndroidProcesses

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jaredrummler/android-processes/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jaredrummler/android-processes) [![License](http://img.shields.io/:license-apache-blue.svg)](LICENSE.txt) [![API](https://img.shields.io/badge/API-4%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=4) <a href="http://www.methodscount.com/?lib=com.jaredrummler%3Aandroid-processes%3A1.0.2" target="_blank"><img src="https://img.shields.io/badge/method count-224-e91e63.svg"></img></a> <a href="http://www.methodscount.com/?lib=com.jaredrummler%3Aandroid-processes%3A1.0.2" target="_blank"><img src="https://img.shields.io/badge/size-22 KB-e91e63.svg"></img></a>

A small library to get the current running processes on Android
___

Why would I need this?
----------------------

Android 5.0+ killed [`getRunningTasks(int)`](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.html#getRunningTasks(int)) and [`getRunningAppProcesses()`](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.html#getRunningAppProcesses()). Both of those methods are now deprecated and only return your application process. You can get a list of running apps using [UsageStatsManager](https://developer.android.com/reference/android/app/usage/UsageStatsManager.html), however, this requires your users to grant your application a special permission in Settings. It has been reported that some OEMs have removed this preference.

This small library can get a list of running apps and does not require any permissions.

Usage
-----

**Get a list of running apps:**

```java
List<AndroidAppProcess> processes = ProcessManager.getRunningAppProcesses();
```

**Get some information about a process**
```java
AndroidAppProcess process = processes.get(location);
String processName = process.name;

Stat stat = process.stat();
int pid = stat.getPid();
int parentProcessId = stat.ppid();
long startTime = stat.stime();
int policy = stat.policy();
char state = stat.state();

Statm statm = process.statm();
long totalSizeOfProcess = statm.getSize();
long residentSetSize = statm.getResidentSetSize();

PackageInfo packageInfo = process.getPackageInfo(context, 0);
String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
```

**Check if your app is in the foreground:**

```java
if (ProcessManager.isMyProcessInTheForeground()) {
  // do stuff
}
```

**Get a list of application processes that are running on the device.**

```java
List<ActivityManager.RunningAppProcessInfo> processes = ProcessManager.getRunningAppProcessInfo(ctx);
```

Limitations
-----------

System apps may not be visible because they have a higher SELinux context then third party apps.

Some information that was available through  [ActivityManager#getRunningAppProcesses()](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.html#getRunningAppProcesses()) is not available using this library ([pkgList](http://developer.android.com/reference/android/app/ActivityManager.RunningAppProcessInfo.html#pkgList), [lru](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.RunningAppProcessInfo.html#lru), [importance](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.RunningAppProcessInfo.html#importance), etc.).

Download
--------

Download [the latest AAR](https://repo1.maven.org/maven2/com/jaredrummler/android-processes/1.0.3/android-processes-1.0.3.aar) or grab via Gradle:

```groovy
compile 'com.jaredrummler:android-processes:1.0.3'
```
or Maven:
```xml
<dependency>
  <groupId>com.jaredrummler</groupId>
  <artifactId>android-processes</artifactId>
  <version>1.0.3</version>
  <type>aar</type>
</dependency>
```

License
--------

    Copyright (C) 2015, Jared Rummler

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
