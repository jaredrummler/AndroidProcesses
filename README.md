<h1 align="center">Android Processes</h1>
<h4 align="center">A small Android library to get the current running processes</h4>

<p align="center">
  <a target="_blank" href="https://developer.android.com/reference/android/os/Build.VERSION_CODES.html#DONUT"><img src="https://img.shields.io/badge/API-4%2B-blue.svg?style=flat" alt="API" /></a>
  <a target="_blank" href="LICENSE.txt"><img src="http://img.shields.io/:license-apache-blue.svg" alt="License" /></a>
  <a target="_blank" href="https://twitter.com/jaredrummler"><img src="https://img.shields.io/twitter/follow/jaredrummler.svg?style=social" /></a>
</p>

___

What is this library for?
-------------------------

This small library can get a list of running processes using root access on an Android device.

Why would I need this?
----------------------

Android 5.0+ killed [`getRunningTasks(int)`](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.html#getRunningTasks(int)) and [`getRunningAppProcesses()`](http://developer.android.com/intl/zh-cn/reference/android/app/ActivityManager.html#getRunningAppProcesses()). Both of those methods are now deprecated and only return your application process. You can get a list of running apps using [UsageStatsManager](https://developer.android.com/reference/android/app/usage/UsageStatsManager.html), however, this requires your users to grant your application a special permission in Settings. It has been reported that some OEMs have removed this preference.

Android Nougat
--------------

Google has significantly restricted access to `/proc` in Android Nougat. This library will not work on Android 7.0. Please [star this issue](https://code.google.com/p/android/issues/detail?id=205565). To get a list of running processes on Android Nougat you will need to use UsageStatsManager or have root access. Really Google?

Usage
-----

```java
// Runs "toolbox ps" in a root shell. This should be run on a background thread.
List<ProcessStatusInfo> processes = ps.run();
for (ProcessStatusInfo process : processes) {
  String processName = process.name;
  int pid = process.pid;
  long rssSize = process.rss;
  // etc.
}
```

Download
--------

Download [the latest AAR](https://repo1.maven.org/maven2/com/jaredrummler/sups/0.0.1/sups-0.0.1.aar) or 
grab via Gradle:

```groovy
compile 'com.jaredrummler:sups:0.0.1'
```

License
--------

    Copyright (C) 2017 Jared Rummler

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
