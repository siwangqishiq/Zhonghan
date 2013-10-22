package com.airad.zhonghan.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.airad.sentry.Sentry;

import net.kencochrane.raven.Dsn;
import net.kencochrane.raven.Raven;
import net.kencochrane.raven.RavenFactory;
import net.kencochrane.raven.event.EventBuilder;
import net.kencochrane.raven.event.interfaces.ExceptionInterface;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";
	public static final String rawDsn = "http://09e484523c4d4f20bfff5d0c93720802:1fee560e549c43d3b065b881cdb97484@118.26.228.170/4";
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static CrashHandler INSTANCE = new CrashHandler();
	private HashMap<String, String> infos = new HashMap<String, String>();
	// Raven raven = RavenFactory.ravenInstance(new Dsn(rawDsn));

	private Context mContext;

	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * public class Example { public static void main(String[] args) { String
	 * rawDsn =
	 * "http://09e484523c4d4f20bfff5d0c93720802:1fee560e549c43d3b065b881cdb97484@118.26.228.170/4"
	 * ; Raven raven = RavenFactory.ravenInstance(new Dsn(rawDsn)); // record a
	 * simple message EventBuilder eventBuilder = new
	 * EventBuilder().setMessage("Hello from Raven!")
	 * .setLevel(Event.Level.ERROR) .setLogger(MyClass.class.getName())
	 * .addSentryInterface(new ExceptionInterface(e));
	 * raven.runBuilderHelpers(eventBuilder); // Optional
	 * raven.sendEvent(eventBuilder.build()); }}
	 */
	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if ((ex != null) && mDefaultHandler != null) {
			Sentry.captureEvent(new Sentry.SentryEventBuilder()
					.setMessage(saveCrashInfo2File(ex)).setException(ex)
					.setTimestamp(System.currentTimeMillis()));
		}
		mDefaultHandler.uncaughtException(thread, ex);
	}

	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		return sb.toString();
		// System.out.println("-->"+sb.toString());
	}

	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}
}// end class
