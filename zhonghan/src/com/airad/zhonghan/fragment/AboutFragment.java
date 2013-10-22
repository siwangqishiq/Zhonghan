package com.airad.zhonghan.fragment;

import com.airad.zhonghan.Config;
import com.airad.zhonghan.MainActivity;
import com.airad.zhonghan.R;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("NewApi")
public class AboutFragment extends Fragment {
	public static final String TAG = "tab_about";
	public static final String requestUrl = Config.host+"/preview/zhonghan/about_us";
	private View view;
	private MainActivity mMainActivity;
	private WebView mAboutWeb;
	private SubAbout mSubAbout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.aboutfrg, container, false);
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mMainActivity = (MainActivity) getActivity();
		mAboutWeb = (WebView) view.findViewById(R.id.about_web);
		if (Build.VERSION.SDK_INT >= 11) {
			mAboutWeb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		mAboutWeb.setBackgroundColor(0);
		WebSettings webSettings = mAboutWeb.getSettings();
		webSettings.setJavaScriptEnabled(true);
		mAboutWeb.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		loadMainPage();
		// mAboutWeb.addJavascriptInterface(new Bridge(), "demo");
	}

	public void loadMainPage() {
		if (mAboutWeb != null)
			mAboutWeb.loadUrl(requestUrl);
	}

	private final class Bridge {
		public void show() {
			// Toast.makeText(getActivity(), "操你妹啊", Toast.LENGTH_SHORT).show();
		}

		public void toChina() {
			toUrl(Config.about_china);
		}

		public void toInertnational() {
			toUrl(Config.about_international);
		}

		private void toUrl(String url) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			if (fm.findFragmentByTag(SubAbout.TAG) == null) {
				FragmentTransaction ft = fm.beginTransaction();
				Fragment frg = SubAbout.newInstance(url);
				ft.add(R.id.about_add, frg, SubAbout.TAG);
				ft.addToBackStack(TAG);
				ft.commit();
			} // end if
		}
	}// end inner class

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}
