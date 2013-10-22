package com.airad.zhonghan.fragment;

import com.airad.zhonghan.R;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SubAbout extends Fragment {
	public static final String TAG = "sub_about";
	private View mainView;
	private View backBtn;
	private WebView mWebView;
	private String dst;

	public static SubAbout newInstance(String dst) {
		SubAbout fragment = new SubAbout();
		fragment.dst = dst;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.sub_aboutl, container, false);
		return mainView;
	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		backBtn = mainView.findViewById(R.id.back_btn);
		backBtn.setOnClickListener(new BackOnClick());

		mWebView = (WebView) mainView.findViewById(R.id.about_web);
		if (Build.VERSION.SDK_INT >= 11) {
			mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		mWebView.setBackgroundColor(0);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		mWebView.loadUrl(dst);
	}

	public void loadUrl(String dst) {
		this.dst = dst;
		mWebView.loadUrl(dst);
	}

	private final class BackOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			fm.popBackStack(AboutFragment.TAG, 1);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}// end class
