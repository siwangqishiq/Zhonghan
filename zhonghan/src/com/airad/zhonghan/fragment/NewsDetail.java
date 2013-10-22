package com.airad.zhonghan.fragment;

import com.airad.zhonghan.R;
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

public class NewsDetail extends Fragment {
	public static final String TAG = "news_detail";
	private View mainView;
	private View backBtn;

	private String url;
	private WebView web;

	public static NewsDetail newInstance(String url) {
		NewsDetail fragment = new NewsDetail();
		fragment.url = url;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.news_detail, container, false);
		return mainView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		backBtn = mainView.findViewById(R.id.back_btn);
		backBtn.setOnClickListener(new BackOnClick());
		web = (WebView)mainView.findViewById(R.id.web);
		WebSettings webSettings = web.getSettings();
		webSettings.setJavaScriptEnabled(true);
		web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true; 
			}
		});
		if(url!=null){
			web.loadUrl(url);
		}
	}
	
	public void loadUrl(String url){
		if(url==null){
			return;
		}
		this.url = url;
		web.loadUrl(url);
	}

	private class BackOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			fm.popBackStack(NewsFragment.TAG, 1);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}// end class
