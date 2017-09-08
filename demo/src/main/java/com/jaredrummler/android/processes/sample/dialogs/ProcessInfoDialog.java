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
import android.text.Spanned;
import com.jaredrummler.android.processes.sample.utils.Utils;
import com.jaredrummler.android.sups.ProcessStatusInfo;
import com.jaredrummler.android.util.HtmlBuilder;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ProcessInfoDialog extends DialogFragment {

  private static final String TAG = "ProcessInfoDialog";

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    ProcessStatusInfo process = getArguments().getParcelable("process");
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

  private Spanned getProcessInfo(ProcessStatusInfo process) {
    HtmlBuilder html = new HtmlBuilder();
    for (Field field : ProcessStatusInfo.class.getDeclaredFields()) {
      if (!Modifier.isStatic(field.getModifiers())) {
        try {
          field.setAccessible(true);
          Object value = field.get(process);
          html.strong(field.getName()).append(' ').append(value).br();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    return html.build();
  }
}
