/*
 * Copyright (C) 2016. Jared Rummler <jared.rummler@gmail.com>
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
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.AndroidProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <p>Helper class to get a list of processes on Android.</p>
 * <hr>
 * <p><strong>Usage:</strong></p>
 *
 * <p>Get a list of running apps:</p>
 * <pre>
 * List&lt;AndroidAppProcess&gt; processes = AndroidProcesses.getRunningAppProcesses();
 * </pre>
 *
 * <p>Get some information about a process:</p>
 * <pre>
 * AndroidAppProcess process = processes.get(location);
 * String processName = process.name;
 *
 * Stat stat = process.stat();
 * int pid = stat.getPid();
 * int parentProcessId = stat.ppid();
 * long startTime = stat.stime();
 * int policy = stat.policy();
 * char state = stat.state();
 *
 * Statm statm = process.statm();
 * long totalSizeOfProcess = statm.getSize();
 * long residentSetSize = statm.getResidentSetSize();
 *
 * PackageInfo packageInfo = process.getPackageInfo(context, 0);
 * String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
 * </pre>
 *
 * <p>Check if your app is in the foreground:</p>
 * <pre>
 * if (AndroidProcesses.isMyProcessInTheForeground()) {
 *   // do stuff
 * }
 * </pre>
 *
 * <p>Get a list of application processes that are running on the device:</p>
 * <pre>
 * List&lt;ActivityManager.RunningAppProcessInfo&gt; processes = AndroidProcesses.getRunningAppProcessInfo(context);
 * </pre>
 *
 * <hr>
 * <p><strong>Limitations</strong></p>
 *
 * <p>System apps may not be visible because they have a higher SELinux context than third party apps.</p>
 * <p>Some information that was available through {@link ActivityManager#getRunningAppProcesses()} is not available
 * using this library
 * ({@link RunningAppProcessInfo#pkgList},
 * {@link RunningAppProcessInfo#lru},
 * {@link RunningAppProcessInfo#importance},
 * etc.).</p>
 * <p>This is currently not working on the N developer preview.</p>
 * <hr>
 * <p><b>Note:</b> You should avoid running methods from this class on the UI thread.</p>
 */
public class AndroidProcesses {

  public static final String TAG = "AndroidProcesses";

  private static final int AID_READPROC = 3009;

  private static boolean loggingEnabled;

  /**
   * Toggle whether debug logging is enabled.
   *
   * @param enabled
   *     {@code true} to enable logging. This should be only be used for debugging purposes.
   * @see #isLoggingEnabled()
   * @see #log(String, Object...)
   * @see #log(Throwable, String, Object...)
   */
  public static void setLoggingEnabled(boolean enabled) {
    loggingEnabled = enabled;
  }

  /**
   * @return {@code true} if logging is enabled.
   * @see #setLoggingEnabled(boolean)
   */
  public static boolean isLoggingEnabled() {
    return loggingEnabled;
  }

  /**
   * Send a log message if logging is enabled.
   *
   * @param message
   *     the message to log
   * @param args
   *     list of arguments to pass to the formatter
   */
  public static void log(String message, Object... args) {
    if (loggingEnabled) {
      Log.d(TAG, args.length == 0 ? message : String.format(message, args));
    }
  }

  /**
   * Send a log message if logging is enabled.
   *
   * @param error
   *     An exception to log
   * @param message
   *     the message to log
   * @param args
   *     list of arguments to pass to the formatter
   */
  public static void log(Throwable error, String message, Object... args) {
    if (loggingEnabled) {
      Log.d(TAG, args.length == 0 ? message : String.format(message, args), error);
    }
  }

  /**
   * On Android 7.0+ the procfs filesystem is now mounted with hidepid=2, eliminating access to the /proc/PID
   * directories of other users. There's a group ("readproc") for making exceptions but it's not exposed as a
   * permission. To get a list of processes on Android 7.0+ you must use {@link android.app.usage.UsageStatsManager}
   * or have root access.
   *
   * @return {@code true} if procfs is mounted with hidepid=2
   */
  public static boolean isProcessInfoHidden() {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader("/proc/mounts"));
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        String[] columns = line.split("\\s+");
        if (columns.length == 6 && columns[1].equals("/proc")) {
          return columns[3].contains("hidepid=1") || columns[3].contains("hidepid=2");
        }
      }
    } catch (IOException e) {
      Log.d(TAG, "Error reading /proc/mounts. Checking if UID 'readproc' exists.");
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ignored) {
        }
      }
    }
    return android.os.Process.getUidForName("readproc") == AID_READPROC;
  }

  /**
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
          log(e, "Error reading from /proc/%d.", pid);
          // System apps will not be readable on Android 5.0+ if SELinux is enforcing.
          // You will need root access or an elevated SELinux context to read all files under /proc.
        }
      }
    }
    return processes;
  }

  /**
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
          log(e, "Error reading from /proc/%d.", pid);
          // System apps will not be readable on Android 5.0+ if SELinux is enforcing.
          // You will need root access or an elevated SELinux context to read all files under /proc.
        }
      }
    }
    return processes;
  }

  /**
   * Get a list of user apps running in the foreground.
   *
   * @param context
   *     the application context
   * @return a list of user apps that are in the foreground.
   */
  public static List<AndroidAppProcess> getRunningForegroundApps(Context context) {
    List<AndroidAppProcess> processes = new ArrayList<>();
    File[] files = new File("/proc").listFiles();
    PackageManager pm = context.getPackageManager();
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
          if (process.foreground
              // ignore system processes. First app user starts at 10000.
              && (process.uid < 1000 || process.uid > 9999)
              // ignore processes that are not running in the default app process.
              && !process.name.contains(":")
              // Ignore processes that the user cannot launch.
              && pm.getLaunchIntentForPackage(process.getPackageName()) != null) {
            processes.add(process);
          }
        } catch (AndroidAppProcess.NotAndroidAppProcessException ignored) {
        } catch (IOException e) {
          log(e, "Error reading from /proc/%d.", pid);
          // System apps will not be readable on Android 5.0+ if SELinux is enforcing.
          // You will need root access or an elevated SELinux context to read all files under /proc.
        }
      }
    }
    return processes;
  }

  /**
   * @return {@code true} if this process is in the foreground.
   */
  public static boolean isMyProcessInTheForeground() {
    try {
      return new AndroidAppProcess(android.os.Process.myPid()).foreground;
    } catch (Exception e) {
      log(e, "Error finding our own process");
    }
    return false;
  }

  /**
   * Returns a list of application processes that are running on the device.
   *
   * <p><b>NOTE:</b> On Lollipop (SDK 22) this does not provide
   * {@link RunningAppProcessInfo#pkgList},
   * {@link RunningAppProcessInfo#importance},
   * {@link RunningAppProcessInfo#lru},
   * {@link RunningAppProcessInfo#importanceReasonCode},
   * {@link RunningAppProcessInfo#importanceReasonComponent},
   * {@link RunningAppProcessInfo#importanceReasonPid},
   * etc. If you need more process information try using
   * {@link #getRunningAppProcesses()} or {@link android.app.usage.UsageStatsManager}</p>
   *
   * @param context
   *     the application context
   * @return a list of RunningAppProcessInfo records, or null if there are no
   * running processes (it will not return an empty list).  This list ordering is not
   * specified.
   */
  public static List<RunningAppProcessInfo> getRunningAppProcessInfo(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
      List<AndroidAppProcess> runningAppProcesses = AndroidProcesses.getRunningAppProcesses();
      List<RunningAppProcessInfo> appProcessInfos = new ArrayList<>();
      for (AndroidAppProcess process : runningAppProcesses) {
        RunningAppProcessInfo info = new RunningAppProcessInfo(process.name, process.pid, null);
        info.uid = process.uid;
        appProcessInfos.add(info);
      }
      return appProcessInfos;
    }
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    return am.getRunningAppProcesses();
  }

  /* package */ AndroidProcesses() {
    throw new AssertionError("no instances");
  }

  /**
   * A {@link Comparator} to list processes by name
   */
  public static final class ProcessComparator implements Comparator<AndroidProcess> {

    @Override public int compare(AndroidProcess p1, AndroidProcess p2) {
      return p1.name.compareToIgnoreCase(p2.name);
    }

  }

}
