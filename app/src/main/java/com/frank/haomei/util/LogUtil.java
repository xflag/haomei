package com.frank.haomei.util;

import android.util.Log;

public class LogUtil {
	public static final int NOTHING = 0;
	public static final int ERROR = 1;
	public static final int WARN = 2;
	public static final int INFO = 3;
	public static final int DEBUG = 4;
	public static final int VERBOS = 5;
	public static final int LOG_LEVEL = VERBOS;
	
	public static void e(String tag, String msg) {
		if (LOG_LEVEL >= ERROR)
			Log.e(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (LOG_LEVEL >= WARN)
			Log.w(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (LOG_LEVEL >= INFO)
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (LOG_LEVEL >= DEBUG)
			Log.d(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (LOG_LEVEL >= VERBOS)
			Log.v(tag, msg);
	}

}
