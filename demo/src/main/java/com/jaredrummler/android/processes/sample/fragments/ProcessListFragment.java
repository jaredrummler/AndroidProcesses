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

package com.jaredrummler.android.processes.sample.fragments;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.jaredrummler.android.processes.sample.adapter.ProcessListAdapter;
import com.jaredrummler.android.processes.sample.dialogs.ProcessInfoDialog;
import com.jaredrummler.android.processes.sample.utils.AndroidAppProcessLoader;
import com.jaredrummler.android.processes.sample.utils.RootChecker;
import com.jaredrummler.android.sups.ProcessStatusInfo;
import java.util.List;

public class ProcessListFragment extends ListFragment implements AndroidAppProcessLoader.Listener, RootChecker.Listener {

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    getListView().setFastScrollEnabled(true);
    new AndroidAppProcessLoader(getActivity(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }

  @Override public void onComplete(List<ProcessStatusInfo> processes) {
    setListAdapter(new ProcessListAdapter(getActivity(), processes));
    if (processes.isEmpty()) {
      new RootChecker(this).execute();
    }
  }

  @Override public void onRootCheckFinished(boolean accessGranted) {
    Toast toast;
    if (accessGranted) {
      toast = Toast.makeText(getActivity(), "Error getting processes. Empty list.", Toast.LENGTH_LONG);
    } else {
      toast = Toast.makeText(getActivity(), "No root access. Cannot get list of processes.", Toast.LENGTH_LONG);
    }
    toast.show();
  }

  @Override public void onListItemClick(ListView l, View v, int position, long id) {
    ProcessStatusInfo process = (ProcessStatusInfo) getListAdapter().getItem(position);
    ProcessInfoDialog dialog = new ProcessInfoDialog();
    Bundle args = new Bundle();
    args.putParcelable("process", process);
    dialog.setArguments(args);
    dialog.show(getActivity().getFragmentManager(), "ProcessInfoDialog");
  }
}
