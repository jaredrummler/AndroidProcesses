/*
 * Copyright (C) 2017 JRummy Apps, Inc. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
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
