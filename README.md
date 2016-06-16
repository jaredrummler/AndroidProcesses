<h1 align="center">AndroidProcesses</h1>
<h4 align="center">A small Android library to get the current running processes</h4>

<p align="center">
  <a target="_blank" href="https://developer.android.com/reference/android/os/Build.VERSION_CODES.html#DONUT"><img src="https://img.shields.io/badge/API-4%2B-blue.svg?style=flat" alt="API" /></a>
  <a target="_blank" href="LICENSE.txt"><img src="http://img.shields.io/:license-apache-blue.svg" alt="License" /></a>
  <a target="_blank" href="https://maven-badges.herokuapp.com/maven-central/com.jaredrummler/android-processes"><img src="https://maven-badges.herokuapp.com/maven-central/com.jaredrummler/android-processes/badge.svg" alt="Maven Central" /></a>
  <a target="_blank" href="http://www.methodscount.com/?lib=com.jaredrummler%3Aandroid-processes%3A1.0.8"><img src="https://img.shields.io/badge/methods-236-e91e63.svg" /></a>
</p>

<p align="center">
  <a target="_blank" href="https://twitter.com/jrummy16"><img src="https://img.shields.io/twitter/follow/jrummy16.svg?style=social" /></a>
</p>

___

What is this library for?
-------------------------

This small library can get a list of running apps and does not require any permissions.

Why would I need this?
----------------------

Android 5.0+ killed [`getRunningTasks(int)`](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.html#getRunningTasks(int)) and [`getRunningAppProcesses()`](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.html#getRunningAppProcesses()). Both of those methods are now deprecated and only return your application process. You can get a list of running apps using [UsageStatsManager](https://developer.android.com/reference/android/app/usage/UsageStatsManager.html), however, this requires your users to grant your application a special permission in Settings. It has been reported that some OEMs have removed this preference.

Usage
-----

**Get a list of running apps:**

```java
List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
```

**Get some information about a process:**

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
if (AndroidProcesses.isMyProcessInTheForeground()) {
  // do stuff
}
```

**Get a list of application processes that are running on the device:**

```java
List<ActivityManager.RunningAppProcessInfo> processes = AndroidProcesses.getRunningAppProcessInfo(ctx);
```

Limitations
-----------

* System apps will not be included on some Android versions because they have a higher SELinux context.
* This is not a full replacement of [getRunningAppProcesses()](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.html#getRunningAppProcesses()). The library does not provide a processes' [pkgList](http://developer.android.com/reference/android/app/ActivityManager.RunningAppProcessInfo.html#pkgList), [lru](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.RunningAppProcessInfo.html#lru), or [importance](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.RunningAppProcessInfo.html#importance).
* This is currently not working on the N developer preview.

Download
--------

Download [the latest AAR](https://repo1.maven.org/maven2/com/jaredrummler/android-processes/1.0.8/android-processes-1.0.8.aar) or grab via Gradle:

```groovy
compile 'com.jaredrummler:android-processes:1.0.8'
```
or Maven:
```xml
<dependency>
  <groupId>com.jaredrummler</groupId>
  <artifactId>android-processes</artifactId>
  <version>1.0.8</version>
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
