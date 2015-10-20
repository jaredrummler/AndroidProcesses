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

package com.jaredrummler.android.processes.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ProcFile extends File implements Parcelable {

  /**
   * Read the contents of a file.
   *
   * @param path
   *     the absolute path to the file.
   * @return the contents of the file.
   * @throws IOException
   *     if an error occurred while reading.
   */
  protected static String readFile(String path) throws IOException {
    StringBuilder output = new StringBuilder();
    BufferedReader reader = new BufferedReader(new FileReader(path));
    output.append(reader.readLine());
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      output.append('\n').append(line);
    }
    reader.close();
    return output.toString();
  }

  public final String content;

  protected ProcFile(String path) throws IOException {
    super(path);
    content = readFile(path);
  }

  protected ProcFile(Parcel in) {
    super(in.readString());
    this.content = in.readString();
  }

  @Override public long length() {
    return content.length();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(getAbsolutePath());
    dest.writeString(this.content);
  }

  public static final Creator<ProcFile> CREATOR = new Creator<ProcFile>() {

    @Override public ProcFile createFromParcel(Parcel in) {
      return new ProcFile(in);
    }

    @Override public ProcFile[] newArray(int size) {
      return new ProcFile[size];
    }
  };

}
