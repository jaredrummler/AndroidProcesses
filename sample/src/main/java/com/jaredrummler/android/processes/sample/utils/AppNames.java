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

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.Hashtable;

public class AppNames {

  static final Hashtable<String, String> APP_NAME_CACHE = new Hashtable<>();

  public static String getLabel(PackageManager pm, PackageInfo packageInfo) {
    if (APP_NAME_CACHE.containsKey(packageInfo.packageName)) {
      return APP_NAME_CACHE.get(packageInfo.packageName);
    }
    String label = packageInfo.applicationInfo.loadLabel(pm).toString();
    APP_NAME_CACHE.put(packageInfo.packageName, label);
    return label;
  }

}
