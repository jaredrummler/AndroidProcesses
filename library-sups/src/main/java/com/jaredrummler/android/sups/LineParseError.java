/*
 * Copyright (C) 2017 JRummy Apps, Inc. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package com.jaredrummler.android.sups;

final class LineParseError extends Exception {

  LineParseError(String message) {
    super(message);
  }

  LineParseError(String message, Throwable cause) {
    super(message, cause);
  }

}
