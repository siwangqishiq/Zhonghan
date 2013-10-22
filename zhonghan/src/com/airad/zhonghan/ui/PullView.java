package com.airad.zhonghan.ui;

import com.airad.zhonghan.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class PullView extends RelativeLayout {
	private Scroller scroller;
	private Context mContext;
	
	public PullView(Context context) {
		super(context);
		mContext = context;
	}
	
	public PullView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init(){
		scroller = new Scroller(mContext);
		
	}
}//end class
