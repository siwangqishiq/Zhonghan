package com.airad.zhonghan.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class IndexView extends View{
	 private static final int SPACE = 20;
     private static final int RADIUS = 5;
     private static final int COLOR_NORMAL = Color.rgb(169, 170, 172),
                     COLOR_HIGHTLIGHT =  Color.rgb(230, 79, 60);
     private int num;
     private int highLightIndex;// 高亮单元 从0开始计数
     private int startX;
     private Paint paintNormal, paintHightLight;

     public IndexView(Context context, AttributeSet attrs, int defStyle) {
             super(context, attrs, defStyle);
             init();
     }

     public IndexView(Context context, AttributeSet attrs) {
             super(context, attrs);
             init();
     }

     public IndexView(Context context) {
             super(context);
             init();
     }

     private void init() {
             num = 0;
             highLightIndex = 0;
             paintNormal = new Paint();
             paintNormal.setColor(COLOR_NORMAL);
             paintNormal.setAntiAlias(true);
             paintHightLight = new Paint();
             paintHightLight.setColor(COLOR_HIGHTLIGHT);
             paintHightLight.setAntiAlias(true);
     }

     /**
      * 设置点数
      * 
      * @param number
      */
     public void setNum(int number) {
             if (number < 0) {
                     return;
             }
             this.num = number;
             this.highLightIndex = 0;
             startX = RADIUS;
     }

     public void prePoint() {
             highLightIndex--;
             if (highLightIndex < 0) {
                     highLightIndex = 0;
             }
     }

     public void nextPoint() {
             highLightIndex++;
             if (highLightIndex > num - 1) {
                     highLightIndex = num - 1;
             }
     }

     public void setPoint(int index) {
             if (index >= 0 && index < num) {
                     this.highLightIndex = index;
             }
             invalidate();
     }

     @Override
     protected void onLayout(boolean changed, int left, int top, int right,
                     int bottom) {
             super.onLayout(changed, left, top, right, bottom);
     }

     @Override
     protected void onDraw(Canvas canvas) {
             super.onDraw(canvas);
             if (num <= 1 || num > 24) {
                     return;
             }
             int y = getHeight() >> 1;
             for (int i = 0; i < num; i++) {
                     if (i != highLightIndex) {
                             canvas.drawCircle(startX + SPACE * i, y, RADIUS, paintNormal);
                     } else {
                             canvas.drawCircle(startX + SPACE * i, y, RADIUS,
                                             paintHightLight);
                     }
             }// end for
     }
}//end class
