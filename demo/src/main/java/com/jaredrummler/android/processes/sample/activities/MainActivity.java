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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jaredrummler.android.processes.sample.fragments.ProcessListFragment;
import com.jaredrummler.android.processes.sample.utils.PsTask;

public class MainActivity extends Activity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      getFragmentManager().beginTransaction().add(android.R.id.content, new ProcessListFragment()).commit();
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, Menu.FIRST, 0, "Run as root");
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case Menu.FIRST:
        new PsTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

}
