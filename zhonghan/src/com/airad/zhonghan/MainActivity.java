package com.airad.zhonghan;

import com.airad.zhonghan.animations.Rotate3dAnimation;
import com.airad.zhonghan.factory.ImageFetcherFactory;
import com.airad.zhonghan.fragment.AboutFragment;
import com.airad.zhonghan.fragment.ColleageFragment;
import com.airad.zhonghan.fragment.HomeFragment;
import com.airad.zhonghan.fragment.MagazineFragment;
import com.airad.zhonghan.fragment.NewsFragment;
import com.airad.zhonghan.ui.components.ImageFetcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	public static final String PUSH_MODULE = "module";
	private long exitTime = 0;
	public TabHost mTabHost;
	public ImageFetcher mImageFetcher;
	private FrameLayout mContainer;

	public LinearLayout foreLayout;
	protected FragmentManager fm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
	}

	private void init() {
		fm = this.getSupportFragmentManager();
		mContainer = (FrameLayout) findViewById(R.id.container);
		mImageFetcher = ImageFetcherFactory.genImageFetcher(this);
		foreLayout = (LinearLayout) findViewById(R.id.frame);
		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup();

		View homeTab = (View) LayoutInflater.from(this).inflate(
				R.layout.indicator_home, null);
		mTabHost.addTab(mTabHost.newTabSpec(HomeFragment.TAG)
				.setIndicator(homeTab).setContent(R.id.tab_home));

		View newsTab = (View) LayoutInflater.from(this).inflate(
				R.layout.indicator_news, null);

		mTabHost.addTab(mTabHost.newTabSpec(NewsFragment.TAG)
				.setIndicator(newsTab).setContent(R.id.tab_news));

		View magzineTab = (View) LayoutInflater.from(this).inflate(
				R.layout.indicator_magzine, null);
		mTabHost.addTab(mTabHost.newTabSpec(MagazineFragment.TAG)
				.setIndicator(magzineTab).setContent(R.id.tab_magazine));

		View colleageTab = (View) LayoutInflater.from(this).inflate(
				R.layout.indicator_colleage, null);
		mTabHost.addTab(mTabHost.newTabSpec(ColleageFragment.TAG)
				.setIndicator(colleageTab).setContent(R.id.tab_colleage));

		View aboutTab = (View) LayoutInflater.from(this).inflate(
				R.layout.indicator_about, null);
		aboutTab.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					Fragment frag = fm.findFragmentById(R.id.tab_about);
					AboutFragment about = (AboutFragment) frag;
					if (about != null)
						about.loadMainPage();
				}
				return false;
			}
		});
		mTabHost.addTab(mTabHost.newTabSpec(AboutFragment.TAG)
				.setIndicator(aboutTab).setContent(R.id.tab_about));
		mTabHost.setOnTabChangedListener(new RotateToFore());
		Intent intent = getIntent();
		int module = intent.getIntExtra(PUSH_MODULE, -1);
		if (module > 0) {
			mTabHost.setCurrentTab(module);
			foreLayout.setVisibility(View.GONE);
			mTabHost.setVisibility(View.VISIBLE);
		}
	}

	private final class RotateToFore implements OnTabChangeListener {
		@Override
		public void onTabChanged(String tabId) {
			if (HomeFragment.TAG.equals(tabId)) {
				applyRotation(0, 90f, R.id.frame);
			}
			if (AboutFragment.TAG.equals(tabId)) {
				Fragment frag = fm.findFragmentById(R.id.tab_about);
				AboutFragment about = (AboutFragment) frag;
				if (about != null)
					about.loadMainPage();
			}
		}
	}// end inner class

	public void applyRotation(float start, float end, final int viewId) {
		final float centerX = mContainer.getWidth() / 2.0f;
		final float centerY = mContainer.getHeight() / 2.0f;
		Rotate3dAnimation rotation = new Rotate3dAnimation(start, end, centerX,
				centerY, 200f, true);
		rotation.setDuration(500);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				mContainer.post(new Runnable() {
					@Override
					public void run() {
						if (viewId == R.id.frame) {
							mTabHost.setVisibility(View.GONE);
							foreLayout.setVisibility(View.VISIBLE);
						} else if (viewId == R.id.tabhost) {
							foreLayout.setVisibility(View.GONE);
							mTabHost.setVisibility(View.VISIBLE);
						}
						Rotate3dAnimation rotatiomAnimation = new Rotate3dAnimation(
								-90, 0, centerX, centerY, 200.0f, false);
						rotatiomAnimation.setDuration(500);
						rotatiomAnimation
								.setInterpolator(new DecelerateInterpolator());
						mContainer.startAnimation(rotatiomAnimation);
					}
				});

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationStart(Animation arg0) {
			}
		});
		mContainer.startAnimation(rotation);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(),
						R.string.pro_exit_cofirm, Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mImageFetcher != null) {
			mImageFetcher.setExitTasksEarly(true);
			mImageFetcher.flushCache();
		}
	}
}// end class
