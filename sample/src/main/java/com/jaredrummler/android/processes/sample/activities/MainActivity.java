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

package com.jaredrummler.android.processes.sample.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidProcess;
import com.jaredrummler.android.processes.sample.fragments.ProcessListFragment;

import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      getFragmentManager().beginTransaction().add(android.R.id.content, new ProcessListFragment()).commit();
    }
    List<AndroidProcess> runningProcesses = ProcessManager.getRunningProcesses();
    for (AndroidProcess runningProcess : runningProcesses) {
      try {
        int uid = runningProcess.status().getUid();
        String uidName = getNameForId(uid);
        if (uidName != null) {
          System.out.println(runningProcess.name);
        }


      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }

  /**
   * Returns the UID/GID name assigned to a particular id, or {@code null} if there is none.
   *
   * @param id
   *     The UID/GID of a process or file
   * @return the name of the UID/GID or {@code null} if the id is unrecognized.
   */
  public static String getNameForId(int id) {
    String name;
    // https://android.googlesource.com/platform/system/core/+/master/include/private/android_filesystem_config.h
    int appId = id - 10000;
    int userId = 0;
    // loop until we get the correct user id.
    // 100000 is the offset for each user.
    while (appId > 100000) {
      appId -= 100000;
      userId++;
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      // u{user_id}_a{app_id} is used on API 17+ for multiple user account support.
      name = String.format("u%d_a%d", userId, appId);
    } else {
      // app_{app_id} is used below API 17.
      name = String.format("app_%d", appId);
    }
    if (android.os.Process.getUidForName(name) == id) {
      return name;
    }
    return null; // unknown UID
  }

}
