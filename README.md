<h1 align="center">AndroidProcesses</h1>
<h4 align="center">A small Android library to get the current running processes</h4>

<p align="center">
  <a target="_blank" href="https://developer.android.com/reference/android/os/Build.VERSION_CODES.html#DONUT"><img src="https://img.shields.io/badge/API-4%2B-blue.svg?style=flat" alt="API" /></a>
  <a target="_blank" href="LICENSE.txt"><img src="http://img.shields.io/:license-apache-blue.svg" alt="License" /></a>
  <a target="_blank" href="https://maven-badges.herokuapp.com/maven-central/com.jaredrummler/android-processes"><img src="https://maven-badges.herokuapp.com/maven-central/com.jaredrummler/android-processes/badge.svg" alt="Maven Central" /></a>
  <a target="_blank" href="http://www.methodscount.com/?lib=com.jaredrummler%3Aandroid-processes%3A1.1.1"><img src="https://img.shields.io/badge/methods-236-e91e63.svg" /></a>
</p>

<p align="center">
  <a target="_blank" href="https://twitter.com/jaredrummler"><img src="https://img.shields.io/twitter/follow/jaredrummler.svg?style=social" /></a>
</p>

___

# PLEASE NOTE, THIS PROJECT IS NO LONGER BEING MAINTAINED

Google has significantly restricted access to `/proc` in Android Nougat. This library will not work on Android 7.0. Please [star this issue](https://code.google.com/p/android/issues/detail?id=205565).

More details can be found at:

* https://jaredrummler.com/2017/09/13/android-processes/
* https://code.google.com/p/android/issues/detail?id=205565

What is this library for?
-------------------------

This small library can get a list of running apps and does not require any permissions.

Why would I need this?
----------------------

Android 5.0+ killed [`getRunningTasks(int)`](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.html#getRunningTasks(int)) and [`getRunningAppProcesses()`](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.html#getRunningAppProcesses()). Both of these methods are now deprecated and only return the caller’s application process.

Android 5.0 introduced [UsageStatsManager](https://developer.android.com/reference/android/app/usage/UsageStatsManager.html) which provides access to device usage history and statistics. This API requires the permission `android.permission.PACKAGE_USAGE_STATS`, which is a system-level permission and will not be granted to third-party apps. However, declaring the permission implies intention to use the API and the user of the device can grant permission through the Settings application.

Usage
-----

**Get a list of running apps:**

```java
// Get a list of running apps
List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();

for (AndroidAppProcess process : processes) {
  // Get some information about the process
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
}
```

Limitations
-----------

* System apps will not be included on some Android versions because they have a higher SELinux context.
* This is not a full replacement of [getRunningAppProcesses()](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.html#getRunningAppProcesses()). The library does not provide a processes' [pkgList](http://developer.android.com/reference/android/app/ActivityManager.RunningAppProcessInfo.html#pkgList), [lru](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.RunningAppProcessInfo.html#lru), or [importance](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.RunningAppProcessInfo.html#importance).
* This is currently not working on the N developer preview.

Download
--------

Download [the latest AAR](https://repo1.maven.org/maven2/com/jaredrummler/android-processes/1.1.1/android-processes-1.1.1.aar) or grab via Gradle:

```groovy
compile 'com.jaredrummler:android-processes:1.1.1'
```
or Maven:
```xml
<dependency>
  <groupId>com.jaredrummler</groupId>
  <artifactId>android-processes</artifactId>
  <version>1.1.1</version>
  <type>aar</type>
</dependency>
```

License
--------

    Copyright (C) 2015 Jared Rummler

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
