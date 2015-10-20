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

package com.jaredrummler.android.sample;

import com.jaredrummler.android.sample.models.AndroidAppProcess;
import com.jaredrummler.android.sample.models.AndroidProcess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessManager {

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

}
