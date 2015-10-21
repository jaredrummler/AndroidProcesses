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

package com.jaredrummler.android.processes.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.AndroidProcess;

import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    for (int id : new int[]{
        R.id.btn_all_processes,
        R.id.btn_running_apps,
        R.id.btn_foreground_apps}) {
      findViewById(id).setOnClickListener(new View.OnClickListener() {

        @Override public void onClick(View v) {
          new ListProcessTask().execute(v.getId());
        }
      });
    }
  }

  private class ListProcessTask extends AsyncTask<Integer, Void, String> {

    @Override protected String doInBackground(Integer... params) {
      StringBuilder sb = new StringBuilder();
      switch (params[0]) {
        case R.id.btn_all_processes: {
          List<AndroidProcess> processes = ProcessManager.getRunningProcesses();
          for (AndroidProcess process : processes) {
            sb.append(process.name).append('\n');
          }
          break;
        }
        case R.id.btn_running_apps: {
          List<AndroidAppProcess> processes = ProcessManager.getRunningAppProcesses();
          for (AndroidAppProcess process : processes) {
            sb.append(process.name).append('\n');
          }
          break;
        }
        case R.id.btn_foreground_apps: {
          List<AndroidAppProcess> processes =
              ProcessManager.getRunningForegroundApps(getApplicationContext());
          Collections.sort(processes, new ProcessManager.ProcessComparator());
          for (AndroidAppProcess process : processes) {
            if (process.foreground) {
              sb.append(process.name).append('\n');
            }
          }
          break;
        }
      }
      return sb.toString();
    }

    @Override protected void onPostExecute(String s) {
      new AlertDialog.Builder(MainActivity.this).setMessage(s).show();
    }
  }

}
