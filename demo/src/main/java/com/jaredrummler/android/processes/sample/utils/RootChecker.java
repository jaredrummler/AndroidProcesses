package com.jaredrummler.android.processes.sample.utils;

import android.os.AsyncTask;
import com.jaredrummler.android.shell.Shell;
import java.lang.ref.WeakReference;

public class RootChecker extends AsyncTask<Void, Void, Boolean> {

  private final WeakReference<Listener> listener;

  public RootChecker(Listener listener) {
    this.listener = new WeakReference<>(listener);
  }

  @Override protected Boolean doInBackground(Void... params) {
    return Shell.SU.available();
  }

  @Override protected void onPostExecute(Boolean result) {
    Listener listener = this.listener.get();
    if (listener != null) {
      listener.onRootCheckFinished(result);
    }
  }

  public interface Listener {

    void onRootCheckFinished(boolean accessGranted);
  }
}
