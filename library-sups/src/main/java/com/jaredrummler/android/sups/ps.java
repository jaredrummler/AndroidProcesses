/*
 * Copyright (C) 2017 JRummy Apps, Inc. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package com.jaredrummler.android.sups;

import android.support.annotation.WorkerThread;
import com.jrummyapps.android.shell.CommandResult;
import com.jrummyapps.android.shell.Shell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class to run {@code toolbox ps -p -P -x -c} in a root shell.
 */
public class ps {

  /**
   * Runs {@code toolbox ps -p -P -x -c} in a root shell.
   *
   * This requires the user to be rooted and grant your application root access.
   *
   * @return list of process status information
   */
  @WorkerThread public static List<ProcessStatusInfo> run() {
    // NOTE: toolbox ps is still included in Android Nougat but could be
    // removed in future Android releases and replaced with toybox ps.
    CommandResult result = Shell.SU.run("toolbox ps -p -P -x -c");
    List<ProcessStatusInfo> processes;
    if (result.isSuccessful()) {
      processes = new ArrayList<>();
      for (String line : result.stdout) {
        try {
          ProcessStatusInfo info = new ProcessStatusInfo(line);
          if ("su".equals(info.name) && android.os.Process.myUid() == android.os.Process.getUidForName(info.user)) {
            continue; // skip this process
          }
          processes.add(info);
        } catch (LineParseError ignored) {
        }
      }
    } else {
      processes = Collections.emptyList();
    }
    return processes;
  }

  private ps() {
    throw new AssertionError();
  }

}
