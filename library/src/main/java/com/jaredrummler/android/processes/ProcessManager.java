/*
 * Copyright (C) 2015. Jared Rummler <jared.rummler@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.jaredrummler.android.processes;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.AndroidProcess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessManager {

  /**
   *
   * @return a list of <i>all</i> processes running on the device.
   */
  public static List<AndroidProcess> getRunningProcesses() {
    List<AndroidProcess> processes = new ArrayList<>();
    File[] files = new File("/proc").listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        int pid;
        try {
          pid = Integer.parseInt(file.getName());
        } catch (NumberFormatException e) {
          continue;
        }
        try {
          processes.add(new AndroidProcess(pid));
        } catch (IOException e) {
          // If you are running this from a third-party app, then system apps will not be
          // readable on Android 5.0+ if SELinux is enforcing. You will need root access or an
          // elevated SELinux context to read all files under /proc.
          // See: https://su.chainfire.eu/#selinux
        }
      }
    }
    return processes;
  }

  /**
   *
   * @return a list of all running app processes on the device.
   */
  public static List<AndroidAppProcess> getRunningAppProcesses() {
    List<AndroidAppProcess> processes = new ArrayList<>();
    File[] files = new File("/proc").listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        int pid;
        try {
          pid = Integer.parseInt(file.getName());
        } catch (NumberFormatException e) {
          continue;
        }
        try {
          processes.add(new AndroidAppProcess(pid));
        } catch (AndroidAppProcess.NotAndroidAppProcessException ignored) {
        } catch (IOException e) {
          // If you are running this from a third-party app, then system apps will not be
          // readable on Android 5.0+ if SELinux is enforcing. You will need root access or an
          // elevated SELinux context to read all files under /proc.
          // See: https://su.chainfire.eu/#selinux
        }
      }
    }
    return processes;
  }

  public static List<AndroidAppProcess> getRunningForegroundApps(Context ctx) {
    List<AndroidAppProcess> processes = new ArrayList<>();
    File[] files = new File("/proc").listFiles();
    PackageManager pm = ctx.getPackageManager();
    for (File file : files) {
      if (file.isDirectory()) {
        int pid;
        try {
          pid = Integer.parseInt(file.getName());
        } catch (NumberFormatException e) {
          continue;
        }
        try {
          AndroidAppProcess process = new AndroidAppProcess(pid);
          if (!process.foreground) {
            // Ignore processes not in the foreground
            continue;
          } else if (process.uid >= 1000 && process.uid <= 9999) {
            // First app user starts at 10000. Ignore system processes.
            continue;
          } else if (process.name.contains(":")) {
            // Ignore processes that are not running in the default app process.
            continue;
          } else if (pm.getLaunchIntentForPackage(process.getPackageName()) == null) {
            // Ignore processes that the user cannot launch.
            // TODO: remove this block?
            continue;
          }
          processes.add(new AndroidAppProcess(pid));
        } catch (AndroidAppProcess.NotAndroidAppProcessException ignored) {
        } catch (IOException e) {
          // If you are running this from a third-party app, then system apps will not be
          // readable on Android 5.0+ if SELinux is enforcing. You will need root access or an
          // elevated SELinux context to read all files under /proc.
          // See: https://su.chainfire.eu/#selinux
        }
      }
    }
    return processes;
  }

  /**
   * Returns a list of application processes that are running on the device.
   *
   * @return a list of RunningAppProcessInfo records, or null if there are no
   * running processes (it will not return an empty list).  This list ordering is not
   * specified.
   */
  public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcessInfo(Context ctx) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
      List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();
      List<ActivityManager.RunningAppProcessInfo> appProcessInfos = new ArrayList<>();
      for (AndroidAppProcess process : runningAppProcesses) {
        ActivityManager.RunningAppProcessInfo info = new ActivityManager.RunningAppProcessInfo(
            process.name, process.pid, null
        );
        info.uid = process.uid;
        // TODO: Get more information about the process. pkgList, importance, lru, etc.
        appProcessInfos.add(info);
      }
      return appProcessInfos;
    }
    ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
    return am.getRunningAppProcesses();
  }

}
