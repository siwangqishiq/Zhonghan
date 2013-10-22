package com.airad.zhonghan;

import org.json.JSONException;

import cn.jpush.android.api.JPushInterface;

import com.airad.sentry.Sentry;
import com.airad.sentry.Sentry.SentryEventBuilder;
import com.airad.sentry.Sentry.SentryEventCaptureListener;
import com.airad.zhonghan.exception.CrashHandler;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyApplication extends Application {
	protected CrashHandler crashHandler;
	@Override
	public void onCreate() {
		super.onCreate();
		
		try {
			JPushInterface.init(this);
			JPushInterface.setAliasAndTags(this,
					Config.jpush_alias, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Sentry.setCaptureListener(new SentryEventCaptureListener() {
			@Override
			public SentryEventBuilder beforeCapture(SentryEventBuilder builder) {
				ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				try {
					builder.getExtra().put("wifi", String.valueOf(mWifi.isConnected()));
				} catch (JSONException e) {}
				return builder;
			}
		});
		// Sentry will look for uncaught exceptions from previous runs and send them        
		Sentry.init(this, CrashHandler.rawDsn);
		crashHandler = new CrashHandler();
		crashHandler.init(this);
	}
}// end class
