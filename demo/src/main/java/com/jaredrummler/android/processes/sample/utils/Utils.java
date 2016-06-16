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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.TypedValue;

import com.jaredrummler.android.processes.models.AndroidAppProcess;

public class Utils {

  public static Bitmap drawableToBitmap(Drawable drawable) {
    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    } else if (drawable instanceof PictureDrawable) {
      PictureDrawable pictureDrawable = (PictureDrawable) drawable;
      Bitmap bitmap = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(),
          pictureDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      canvas.drawPicture(pictureDrawable.getPicture());
      return bitmap;
    }
    int width = drawable.getIntrinsicWidth();
    width = width > 0 ? width : 1;
    int height = drawable.getIntrinsicHeight();
    height = height > 0 ? height : 1;
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);
    return bitmap;
  }

  public static int toPx(Context context, float dp) {
    Resources r = context.getResources();
    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    return Math.round(px);
  }

  public static String getName(Context context, AndroidAppProcess process) {
    try {
      PackageManager pm = context.getPackageManager();
      PackageInfo packageInfo = process.getPackageInfo(context, 0);
      return AppNames.getLabel(pm, packageInfo);
    } catch (PackageManager.NameNotFoundException e) {
      return process.name;
    }
  }

}
