package com.airad.zhonghan.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.airad.zhonghan.Config;
import com.airad.zhonghan.MainActivity;
import com.airad.zhonghan.R;
import com.airad.zhonghan.business.NetService;
import com.airad.zhonghan.data.Constants;
import com.airad.zhonghan.model.Home;
import com.airad.zhonghan.ui.RefreshableView;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment  implements
RefreshableView.RefreshListener{
	public static final String TAG="tab_home";
	public static final String REQUEST=Config.host+"/api/zhonghan/home";
	
	private View view;
	private MainActivity mMainActivity;
	private TextView newsIcon,magzineIcon,colleageIcon,aboutIcon;
	private RefreshableView mRefresh;
	private NetTask mNetTask;
	
	private TextView newsNum,magzineNum,colleageNum;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view =  inflater.inflate(R.layout.homefrg, container,
				false);
		newsIcon = (TextView)view.findViewById(R.id.news_icon);
		magzineIcon = (TextView)view.findViewById(R.id.magzine_icon);
		colleageIcon = (TextView)view.findViewById(R.id.colleage_icon);
		aboutIcon = (TextView)view.findViewById(R.id.about_icon);
		mRefresh = (RefreshableView)view.findViewById(R.id.mypull);
		newsNum = (TextView)view.findViewById(R.id.news_num);
		magzineNum= (TextView)view.findViewById(R.id.magzine_num);
		colleageNum =  (TextView)view.findViewById(R.id.colleage_num);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mMainActivity = (MainActivity)getActivity();
		newsIcon.setOnClickListener(new RotateToBack(1));
		magzineIcon.setOnClickListener(new RotateToBack(2));
		colleageIcon.setOnClickListener(new RotateToBack(3));
		aboutIcon.setOnClickListener(new RotateToBack(4));
		mRefresh.setRefreshListener(this);
	}
	
	private final class RotateToBack implements OnClickListener {
		private int index;
		public RotateToBack(int index){
			this.index = index;
		}
		
		@Override
		public void onClick(View v) {
			mMainActivity.mTabHost.setCurrentTab(index);
//			mMainActivity.mTabHost.getTabWidget().setCurrentTab(index);
			mMainActivity.applyRotation(0, 90f, R.id.tabhost);
		}
	}// end inner class

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onRefresh(RefreshableView view) {
		if(mNetTask!=null){
			mNetTask.cancel(true);
		}
		mNetTask= new NetTask();
		mNetTask.execute(-1l);
	}
	
	private Home request() throws Exception{
		SharedPreferences sp = getActivity().getSharedPreferences(Config.PACKAGE, Context.MODE_PRIVATE);
		long newsTs = sp.getLong(Constants.NEWS_LAST_UPDATETIME, 0);
		long magzineTs = sp.getLong(Constants.MAGZINE_LAST_UPDATETIME, 0);
		long colleageTs=sp.getLong(Constants.COLLEAGE_LAST_UPDATETIME, 0);
		NetService netService = new NetService();
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("news_ts",newsTs+""));
		params.add(new BasicNameValuePair("emag_ts",magzineTs+""));
		params.add(new BasicNameValuePair("college_ts",colleageTs+""));
		System.out.println(newsTs+","+magzineTs+","+colleageTs);
		String origin_str = netService.sendGet(REQUEST, params);
		return Home.instanceFromOrigin(origin_str);
	}
	
	private final class NetTask extends AsyncTask<Long, Void, Home>{
		@Override
		protected Home doInBackground(Long... params) {
			try {
				return request();
//				return Home.instanceFromOrigin("{'response': {'news':'10','emag':'5','college':'10'},'error_code':'' ,'error_msg':'操你妹的错误啊' }");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Home result) {
			super.onPostExecute(result);
			if(result==null){
				Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
				mRefresh.finishRefresh();
				return;
			}
			
			if(result.getErrorCode()!=null){
				Toast.makeText(getActivity(), result.getErrorMsg(), Toast.LENGTH_SHORT).show();
				mRefresh.finishRefresh();
				return;
			}
			
			//设置更新信息条目数
			int news_num = result.getNews();
			int magzine_num = result.getEmag();
			int colleage_num = result.getColleage();
			if(news_num>0){
				newsNum.setText(news_num+"");
				newsNum.setVisibility(View.VISIBLE);
			}else{
				newsNum.setText("");
				newsNum.setVisibility(View.GONE);
			}
			if(magzine_num>0){
				magzineNum.setText(magzine_num+"");
				magzineNum.setVisibility(View.VISIBLE);
			}else{
				magzineNum.setText("");
				magzineNum.setVisibility(View.GONE);
			}
			if(colleage_num>0){
				colleageNum.setText(colleage_num+"");
				colleageNum.setVisibility(View.VISIBLE);
			}else{
				colleageNum.setText("");
				colleageNum.setVisibility(View.GONE);
			}
			
			mRefresh.finishRefresh();//关闭下拉框
			
//			/*重写上次更新时间*/
//			SharedPreferences sp = getActivity().getSharedPreferences(Config.PACKAGE, Context.MODE_PRIVATE);
//			Editor editor = sp.edit();
//			if(tempTs>0){
//				editor.putLong(Constants.NEWS_LAST_UPDATETIME, tempTs);
//				editor.putLong(Constants.MAGZINE_LAST_UPDATETIME, tempTs);
//				editor.putLong(Constants.COLLEAGE_LAST_UPDATETIME, tempTs);
//			}
//			editor.commit();
		}
	}//end class

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mNetTask!=null){
			mNetTask.cancel(true);
		}
	}
}//end class
