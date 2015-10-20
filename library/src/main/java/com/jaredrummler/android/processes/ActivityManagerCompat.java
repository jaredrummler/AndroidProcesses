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
import android.os.Build;

import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActivityManagerCompat {

  private final ActivityManager am;

  public ActivityManagerCompat(Context context) {
    am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
  }

  /**
   * Returns a list of application processes that are running on the device.
   *
   * @return a list of RunningAppProcessInfo records, or null if there are no
   * running processes (it will not return an empty list).  This list ordering is not
   * specified.
   */
  public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
      List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();
      List<ActivityManager.RunningAppProcessInfo> appProcessInfos = new ArrayList<>();
      for (AndroidAppProcess process : runningAppProcesses) {
        ActivityManager.RunningAppProcessInfo info = new ActivityManager.RunningAppProcessInfo(
            process.name, process.pid, null
        );
        try {
          info.uid = process.status().getUid();
        } catch (IOException ignored) {
        }
        // TODO: Get more information about the process. pkgList, importance, lru, etc.
        appProcessInfos.add(info);
      }
      return appProcessInfos;
    }
    return am.getRunningAppProcesses();
  }

}
