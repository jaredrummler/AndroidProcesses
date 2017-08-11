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

package com.jaredrummler.android.processes.sample.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import com.jaredrummler.android.sups.ProcessStatusInfo;
import com.jaredrummler.android.sups.ps;
import com.jaredrummler.android.util.HtmlBuilder;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Runs ps in a root shell and shows a dialog of processes. Application processes are in green.
 */
public class PsTask extends AsyncTask<Void, Void, List<ProcessStatusInfo>> {

  private final WeakReference<Activity> weakActivity;
  private ProgressDialog dialog;

  public PsTask(Activity activity) {
    weakActivity = new WeakReference<>(activity);
  }

  @Override protected void onPreExecute() {
    dialog = new ProgressDialog(weakActivity.get());
    dialog.setMessage("Please wait...");
    dialog.show();
  }

  @Override protected List<ProcessStatusInfo> doInBackground(Void... params) {
    return ps.run();
  }

  @Override protected void onPostExecute(List<ProcessStatusInfo> infos) {
    dialog.dismiss();
    Activity activity = weakActivity.get();
    if (activity != null && !activity.isFinishing()) {
      HtmlBuilder html = new HtmlBuilder();
      for (ProcessStatusInfo info : infos) {
        if (info.isApp()) {
          // app processes are in green
          html.font(Color.GREEN, info.name);
        } else {
          html.append(info.name);
        }
        html.br();
      }
      new AlertDialog.Builder(activity)
          .setTitle("Processes")
          .setMessage(html.build())
          .show();
    }
  }

}
