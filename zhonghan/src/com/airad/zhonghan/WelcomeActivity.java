package com.airad.zhonghan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class WelcomeActivity extends Activity {
	private SkipHandler handler = new SkipHandler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		handler.sendEmptyMessageDelayed(1, 2000);
	}
	
	private final class SkipHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent it = new Intent(WelcomeActivity.this,MainActivity.class);
			WelcomeActivity.this.startActivity(it);
			WelcomeActivity.this.finish();
		}
	}//end inner class
}// end class
