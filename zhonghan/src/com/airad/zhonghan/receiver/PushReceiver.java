package com.airad.zhonghan.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import com.airad.zhonghan.MainActivity;
import com.airad.zhonghan.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

/**
 * 处理推送信息
 * 
 * @author panyi
 * 
 */
public class PushReceiver extends BroadcastReceiver {
	protected Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {// 推送的消息
			mContext = context;
			Bundle bundle = intent.getExtras();
			String orignJson = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			// System.out.println("自定义消息-->" + orignJson);
			// Toast.makeText(mContext, orignJson, Toast.LENGTH_LONG).show();
			showNotification(orignJson);
		}
	}

	private void showNotification(String origin) {
		try {
			JSONObject obj = new JSONObject(origin);
			String title = obj.getString("title");
			int module = obj.getInt("module");

			Notification notification = new Notification();
			notification.icon = R.drawable.ic_launcher;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults |= Notification.DEFAULT_SOUND;
			Intent intent = new Intent();
			intent.setClass(mContext, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(MainActivity.PUSH_MODULE, module);
			PendingIntent contentIntent = PendingIntent.getActivity(mContext,
					1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			notification.setLatestEventInfo(mContext, title, title,
					contentIntent);
			NotificationManager nManager = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nManager.notify(100, notification);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}// end class
