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

package com.jaredrummler.android.processes.sample.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Spanned;
import android.text.format.Formatter;
import android.util.Log;

import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.Stat;
import com.jaredrummler.android.processes.models.Statm;
import com.jaredrummler.android.processes.models.Status;
import com.jaredrummler.android.processes.sample.utils.HtmlBuilder;
import com.jaredrummler.android.processes.sample.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProcessInfoDialog extends DialogFragment {

  private static final String TAG = "ProcessInfoDialog";

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    AndroidAppProcess process = getArguments().getParcelable("process");
    return new AlertDialog.Builder(getActivity())
        .setTitle(Utils.getName(getActivity(), process))
        .setMessage(getProcessInfo(process))
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();
  }

  private Spanned getProcessInfo(AndroidAppProcess process) {
    HtmlBuilder html = new HtmlBuilder();

    html.p().strong("NAME: ").append(process.name).close();
    html.p().strong("POLICY: ").append(process.foreground ? "fg" : "bg").close();
    html.p().strong("PID: ").append(process.pid).close();

    try {
      Status status = process.status();
      html.p().strong("UID/GID: ").append(status.getUid()).append('/').append(status.getGid()).close();
    } catch (IOException e) {
      Log.d(TAG, String.format("Error reading /proc/%d/status.", process.pid));
    }

    // should probably be run in a background thread.
    try {
      Stat stat = process.stat();
      html.p().strong("PPID: ").append(stat.ppid()).close();
      long bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
      long startTime = bootTime + (10 * stat.starttime());
      SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy KK:mm:ss a", Locale.getDefault());
      html.p().strong("START TIME: ").append(sdf.format(startTime)).close();
      html.p().strong("CPU TIME: ").append((stat.stime() + stat.utime()) / 100).close();
      html.p().strong("NICE: ").append(stat.nice()).close();
      int rtPriority = stat.rt_priority();
      if (rtPriority == 0) {
        html.p().strong("SCHEDULING PRIORITY: ").append("non-real-time").close();
      } else if (rtPriority >= 1 && rtPriority <= 99) {
        html.p().strong("SCHEDULING PRIORITY: ").append("real-time").close();
      }
      long userModeTicks = stat.utime();
      long kernelModeTicks = stat.stime();
      long percentOfTimeUserMode;
      long percentOfTimeKernelMode;
      if ((kernelModeTicks + userModeTicks) > 0) {
        percentOfTimeUserMode = (userModeTicks * 100) / (userModeTicks + kernelModeTicks);
        percentOfTimeKernelMode = (kernelModeTicks * 100) / (userModeTicks + kernelModeTicks);
        html.p().strong("TIME EXECUTED IN USER MODE: ").append(percentOfTimeUserMode + "%").close();
        html.p().strong("TIME EXECUTED IN KERNEL MODE: ").append(percentOfTimeKernelMode + "%").close();
      }
    } catch (IOException e) {
      Log.d(TAG, String.format("Error reading /proc/%d/stat.", process.pid));
    }

    try {
      Statm statm = process.statm();
      html.p().strong("SIZE: ").append(Formatter.formatFileSize(getActivity(), statm.getSize())).close();
      html.p().strong("RSS: ").append(Formatter.formatFileSize(getActivity(), statm.getResidentSetSize())).close();
    } catch (IOException e) {
      Log.d(TAG, String.format("Error reading /proc/%d/statm.", process.pid));
    }

    try {
      html.p().strong("OOM SCORE: ").append(process.oom_score()).close();
    } catch (IOException e) {
      Log.d(TAG, String.format("Error reading /proc/%d/oom_score.", process.pid));
    }

    try {
      html.p().strong("OOM ADJ: ").append(process.oom_adj()).close();
    } catch (IOException e) {
      Log.d(TAG, String.format("Error reading /proc/%d/oom_adj.", process.pid));
    }

    try {
      html.p().strong("OOM SCORE ADJ: ").append(process.oom_score_adj()).close();
    } catch (IOException e) {
      Log.d(TAG, String.format("Error reading /proc/%d/oom_score_adj.", process.pid));
    }

    return html.toSpan();
  }
}
