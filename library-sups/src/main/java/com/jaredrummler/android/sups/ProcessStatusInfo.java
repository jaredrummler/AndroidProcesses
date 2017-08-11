/*
 * Copyright (C) 2017 Jared Rummler
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
 */

package com.jaredrummler.android.sups;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Process info retrieved from running {@code ps} in a root shell.
 */
public class ProcessStatusInfo implements Parcelable {

  private static final Pattern PS_LINE_PATTERN = Pattern.compile(
      "^(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(-?\\d+)\\s+(-?\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(bg|fg|un|er)?\\s+(\\S+)\\s+(\\S+)\\s+(D|R|S|T|W|X|Z)\\s+(\\S+)\\s+\\(u:(\\d+),\\s+s:(\\d+)\\)$");
  private static final Pattern MULTI_USER_APP_ID = Pattern.compile("^u\\d+_a\\d+$");
  private static final Pattern DEPRECATED_APP_ID = Pattern.compile("^app_\\d+$");

  /** User name */
  public final String user;

  /** User ID */
  public final int uid;

  /** Processes ID */
  public final int pid;

  /** Parent processes ID */
  public final int ppid;

  /** virtual memory size of the process in KiB (1024-byte units). */
  public final long vsize;

  /** resident set size, the non-swapped physical memory that a task has used (in kiloBytes). */
  public final long rss;

  public final int cpu;

  /** The priority */
  public final int priority;

  /** The priority, <a href="https://en.wikipedia.org/wiki/Nice_(Unix)">niceness</a> level */
  public final int niceness;

  /** Real time priority */
  public final int realTimePriority;

  /** 0 (sched_other), 1 (sched_fifo), and 2 (sched_rr). */
  public final int schedulingPolicy;

  /** The scheduling policy. Either "bg", "fg", "un", "er", or "" */
  public final String policy;

  /** address of the kernel function where the process is sleeping */
  public final String wchan;

  public final String pc;

  /**
   * Possible states:
   * <p/>
   * "D" uninterruptible sleep (usually IO)
   * <p/>
   * "R" running or runnable (on run queue)
   * <p/>
   * "S" interruptible sleep (waiting for an event to complete)
   * <p/>
   * "T" stopped, either by a job control signal or because it is being traced
   * <p/>
   * "W" paging (not valid since the 2.6.xx kernel)
   * <p/>
   * "X" dead (should never be seen)
   * </p>
   * "Z" defunct ("zombie") process, terminated but not reaped by its parent
   */
  public final String state;

  /** The process name */
  public final String name;

  /** user time in milliseconds */
  public final long userTime;

  /** system time in milliseconds */
  public final long systemTime;

  ProcessStatusInfo(String line) throws LineParseError {
    Matcher matcher = PS_LINE_PATTERN.matcher(line);
    if (!matcher.matches()) {
      throw new LineParseError("The line does not match the expected output");
    }
    try {
      user = matcher.group(1);
      pid = Integer.parseInt(matcher.group(2));
      ppid = Integer.parseInt(matcher.group(3));
      vsize = Integer.parseInt(matcher.group(4)) * 1024;
      rss = Integer.parseInt(matcher.group(5)) * 1024;
      cpu = Integer.parseInt(matcher.group(6));
      priority = Integer.parseInt(matcher.group(7));
      niceness = Integer.parseInt(matcher.group(8));
      realTimePriority = Integer.parseInt(matcher.group(9));
      schedulingPolicy = Integer.parseInt(matcher.group(10));
      policy = matcher.group(11);
      wchan = matcher.group(12);
      pc = matcher.group(13);
      state = matcher.group(14);
      name = matcher.group(15);
      userTime = Integer.parseInt(matcher.group(16));
      systemTime = Integer.parseInt(matcher.group(17));
      uid = android.os.Process.getUidForName(user);
    } catch (Exception e) {
      throw new LineParseError("Error parsing line '" + line + "'", e);
    }
  }

  public boolean isApp() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      // u{user_id}_a{app_id} is used on API 17+ for multiple user account support.
      return MULTI_USER_APP_ID.matcher(user).matches();
    }
    // app_{app_id} is used below API 17.
    return DEPRECATED_APP_ID.matcher(user).matches();
  }

  @Override public String toString() {
    return "ProcessInfo{" +
        "user='" + user + '\'' +
        ", uid=" + uid +
        ", pid=" + pid +
        ", ppid=" + ppid +
        ", vsize=" + vsize +
        ", rss=" + rss +
        ", cpu=" + cpu +
        ", priority=" + priority +
        ", niceness=" + niceness +
        ", realTimePriority=" + realTimePriority +
        ", schedulingPolicy=" + schedulingPolicy +
        ", policy='" + policy + '\'' +
        ", wchan='" + wchan + '\'' +
        ", pc='" + pc + '\'' +
        ", state='" + state + '\'' +
        ", name='" + name + '\'' +
        ", userTime=" + userTime +
        ", systemTime=" + systemTime +
        '}';
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.user);
    dest.writeInt(this.uid);
    dest.writeInt(this.pid);
    dest.writeInt(this.ppid);
    dest.writeLong(this.vsize);
    dest.writeLong(this.rss);
    dest.writeInt(this.cpu);
    dest.writeInt(this.priority);
    dest.writeInt(this.niceness);
    dest.writeInt(this.realTimePriority);
    dest.writeInt(this.schedulingPolicy);
    dest.writeString(this.policy);
    dest.writeString(this.wchan);
    dest.writeString(this.pc);
    dest.writeString(this.state);
    dest.writeString(this.name);
    dest.writeLong(this.userTime);
    dest.writeLong(this.systemTime);
  }

  protected ProcessStatusInfo(Parcel in) {
    this.user = in.readString();
    this.uid = in.readInt();
    this.pid = in.readInt();
    this.ppid = in.readInt();
    this.vsize = in.readLong();
    this.rss = in.readLong();
    this.cpu = in.readInt();
    this.priority = in.readInt();
    this.niceness = in.readInt();
    this.realTimePriority = in.readInt();
    this.schedulingPolicy = in.readInt();
    this.policy = in.readString();
    this.wchan = in.readString();
    this.pc = in.readString();
    this.state = in.readString();
    this.name = in.readString();
    this.userTime = in.readLong();
    this.systemTime = in.readLong();
  }

  public static final Parcelable.Creator<ProcessStatusInfo> CREATOR = new Parcelable.Creator<ProcessStatusInfo>() {
    @Override public ProcessStatusInfo createFromParcel(Parcel source) {
      return new ProcessStatusInfo(source);
    }

    @Override public ProcessStatusInfo[] newArray(int size) {
      return new ProcessStatusInfo[size];
    }
  };

}
