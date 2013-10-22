package com.airad.zhonghan.fragment;

import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.http.message.BasicNameValuePair;

import com.airad.zhonghan.Config;
import com.airad.zhonghan.MainActivity;
import com.airad.zhonghan.R;
import com.airad.zhonghan.business.NetService;
import com.airad.zhonghan.data.Constants;
import com.airad.zhonghan.factory.ImageFetcherFactory;
import com.airad.zhonghan.model.ColleageMsg;
import com.airad.zhonghan.model.ColleagePage;
import com.airad.zhonghan.model.News;
import com.airad.zhonghan.model.NewsPage;
import com.airad.zhonghan.model.PicNews;
import com.airad.zhonghan.ui.IndexView;
import com.airad.zhonghan.ui.components.ImageFetcher;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ColleageFragment extends Fragment {
	public static final String TAG = "tab_colleage";
	public static final String requestUrl = Config.host
			+ "/api/zhonghan/colleges";
	public static final int pageSize = 10;
	private int page = 1;
	private long timeStamp = 0;
	private ImageFetcher mImageFetcher;
	private LinkedList<String> mListItems;
	private LinkedList<ColleageMsg> dataList;
	private LinkedList<ColleageMsg> normalNewsList;
	private ImagePagerAdapter mPagerAdapter;
	private View mainView;
	private MainActivity mMainActivity;
	private ViewPager mGallery;
	private TextView newsGalleryText;
	private NormalNewsListAdapter normalListAdapter;
	private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;
	private GetDataTask mTask;
	private ColleageMsgDetail colleageMsgDetail;
	private UpdateTask mUpdateTask;
	private MoreTask mMoreTask;
	private IndexView mIndexView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.colleagefrg, container, false);
		return mainView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mMainActivity = (MainActivity) getActivity();
		mImageFetcher = ImageFetcherFactory.genImageFetcher(getActivity(),
				R.drawable.ic_action_search);

		mPullRefreshListView = (PullToRefreshListView) mainView
				.findViewById(R.id.colleage_pullList);
		mPullRefreshListView.setMode(Mode.BOTH);// 上下都可拉动

		actualListView = mPullRefreshListView.getRefreshableView();
		View galleryView = LayoutInflater.from(mMainActivity).inflate(
				R.layout.colleage_gallery, null);
		mIndexView = (IndexView)galleryView.findViewById(R.id.indexView);
		mGallery = (ViewPager) galleryView
				.findViewById(R.id.my_colleage_gallery);
		mGallery.setVisibility(View.VISIBLE);
		newsGalleryText = (TextView) galleryView
				.findViewById(R.id.my_colleage_gallery_text);

		actualListView.addHeaderView(galleryView);

		if (mTask != null) {
			mTask.cancel(true);
		}
		mTask = new GetDataTask();
		mTask.execute(1);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mTask != null) {
			mTask.cancel(true);
		}
		if (mMoreTask != null) {
			mMoreTask.cancel(true);
		}
		if (mUpdateTask != null) {
			mUpdateTask.cancel(true);
		}
	}

	private final class ListClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long index) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			if (fm.findFragmentByTag(ColleageMsgDetail.TAG) == null) {
				ColleageMsg item = normalNewsList.get((int) index);
				FragmentTransaction ft = fm.beginTransaction();
				if (colleageMsgDetail == null) {
					colleageMsgDetail = ColleageMsgDetail.newInstance(item
							.getWebUrl());
				} else {
					colleageMsgDetail.loadUrl(item.getWebUrl());
				}
				
				ImageView isNew = (ImageView) view
						.findViewById(R.id.is_new_flag);
				isNew.setVisibility(View.INVISIBLE);
				item.setTimeStamp(0L);
				SharedPreferences sp = getActivity().getSharedPreferences(
						Config.PACKAGE, Context.MODE_PRIVATE);
				String colleageMap = sp.getString(Constants.COLLEAGE_MAP, "");
				if(colleageMap.indexOf(item.getId()+"")==-1){
					colleageMap += ("#" + item.getId());
					sp.edit().putString(Constants.COLLEAGE_MAP, colleageMap).commit();
				}
				
				ft.add(R.id.colleage_add, colleageMsgDetail,
						ColleageMsgDetail.TAG);
				ft.addToBackStack(TAG);
				ft.commit();
			} // end if
		}
	}// end inner class

	private final class NormalNewsListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public NormalNewsListAdapter() {
			mInflater = LayoutInflater.from(getActivity());
		}

		@Override
		public int getCount() {
			return normalNewsList.size();
		}

		@Override
		public Object getItem(int position) {
			return normalNewsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return (long) position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.colleage_msg_item,
						null);
			}
			ImageView typeImg = (ImageView) convertView.findViewById(R.id.type);
			ImageView isnew = (ImageView) convertView
					.findViewById(R.id.is_new_flag);
			isnew.setVisibility(View.INVISIBLE);
			TextView title = (TextView) convertView.findViewById(R.id.title);
			TextView date = (TextView) convertView.findViewById(R.id.date);
			TextView content = (TextView) convertView
					.findViewById(R.id.content);

			ColleageMsg item = normalNewsList.get(position);

			title.setText(item.getTitle());
			date.setText(item.getDate());
			content.setText(item.getContent());

			int type_id = R.drawable.colleage_msgtype_red;
			switch (item.getType()) {
			case ColleageMsg.TYPE_CONGRESS:
				type_id = R.drawable.colleage_msgtype_red;
				break;
			case ColleageMsg.TYPE_TRAIN:
				type_id = R.drawable.colleage_msgtype_green;
				break;
			case ColleageMsg.TYPE_EXPIRED:
				type_id = R.drawable.colleage_msgtype_gray;
				break;
			}
			typeImg.setImageResource(type_id);
			if ((System.currentTimeMillis() - (item.getTimeStamp() * 1000) <= Constants.DAY_DELTA)&&
					!isHasVisitColleage(item.getId()+"")) {
				isnew.setVisibility(View.VISIBLE);
			}

			return convertView;
		}
	}// end inner class
	
	private boolean isHasVisitColleage(String id) {
		String colleageMap = getActivity().getSharedPreferences(Config.PACKAGE,
				Context.MODE_PRIVATE).getString(Constants.COLLEAGE_MAP, "");
		if (colleageMap.indexOf(id) != -1) {
			return true;
		} else {
			return false;
		}
	}
	
	private void limitDataListNum(){
		while(dataList.size()>3){
			dataList.removeLast();
		}//end while
	}

	private class GetDataTask extends AsyncTask<Integer, Void, ColleagePage> {
		@Override
		protected ColleagePage doInBackground(Integer... args) {
			NetService netService = new NetService();
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("page_size", pageSize + ""));
			params.add(new BasicNameValuePair("page", page + ""));
			try {
				String originStr = netService.sendGet(requestUrl, params);
				return ColleagePage.instanceFromOrigin(originStr);
				// return ColleagePage
				// .instanceFromOrigin("{response:{colleges:[{id:1,thumb_pic:'http://d.hiphotos.baidu.com/album/w%3D2048/sign=9c31295dac345982c58ae29238cc30ad/f2deb48f8c5494eefbf3b2302cf5e0fe98257ec1.jpg',type:1,title:'学院信息',publish_time:'2013-01-13',content_url:'http://music.baidu.com',category:1,ts:0},{id:1,thumb_pic:'http://e.hiphotos.baidu.com/album/w%3D2048/sign=d278f2b5d0160924dc25a51be03f34fa/1f178a82b9014a909286d35ea9773912b21bee9d.jpg',type:1,title:'学院信息2',publish_time:'2013-01-13',content_url:'http://music.baidu.com',category:1,ts:0},{id:1,thumb_pic:'http://b.hiphotos.baidu.com/album/w%3D2048/sign=e1feca94fbf2b211e42e824efeb86738/8435e5dde71190ef87003e38ce1b9d16fcfa605a.jpg',type:1,title:'学院信息3',publish_time:'2013-01-13',content_url:'http://music.baidu.com',category:1,ts:0},{id:1,thumb_pic:'',type:0,title:'学院信息',publish_time:'2013-01-13',content_url:'http://music.baidu.com',category:1,ts:0},{id:1,thumb_pic:'',type:0,title:'学院信息4',publish_time:'2013-01-13',content_url:'http://music.baidu.com',category:0,ts:0},{id:1,thumb_pic:'',type:0,title:'学院信息5',publish_time:'2013-01-13',content_url:'http://music.baidu.com',category:0,ts:0},{id:1,thumb_pic:'',type:0,title:'学院信息6',publish_time:'2013-01-13',content_url:'http://music.baidu.com',category:0,ts:0}]},error_code:'',error_msg:''}");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ColleagePage result) {
			super.onPostExecute(result);
			mPullRefreshListView.onRefreshComplete();

			if (result == null) {
				Toast.makeText(getActivity(), R.string.net_error,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (result.getErrorCode() != null) {
				Toast.makeText(getActivity(), result.getErrorMsg(),
						Toast.LENGTH_SHORT).show();
				return;
			}

			dataList = result.getPicList();
			limitDataListNum();
			if(dataList.size()>0){
				if (dataList.get(0) != null) {
					newsGalleryText.setText(dataList.get(0).getTitle());
				}
			}
			mIndexView.setNum(dataList.size());
			normalNewsList = result.getNormalList();
			page++;// 页数加1
			if (result.getTimeStamp() != null){
				timeStamp = result.getTimeStamp();// 更新时间戳
				
				SharedPreferences sp = getActivity().getSharedPreferences(Config.PACKAGE, Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putLong(Constants.COLLEAGE_LAST_UPDATETIME, timeStamp);
				editor.commit();
			}
			mPagerAdapter = new ImagePagerAdapter(getActivity()
					.getSupportFragmentManager());
			mGallery.setAdapter(mPagerAdapter);
			normalListAdapter = new NormalNewsListAdapter();
			actualListView.setAdapter(normalListAdapter);
			actualListView.setOnItemClickListener(new ListClick());
			// 注册上拉 下拉事件响应
			mPullRefreshListView.setOnRefreshListener(new PullListener());
			mGallery.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageSelected(int index) {
					newsGalleryText.setText(dataList.get(index).getTitle());
					mIndexView.setPoint(index);
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
		}
	}

	private final class PullListener implements OnRefreshListener2 {
		/**
		 * 头部拉动 更新新内容
		 */
		@Override
		public void onPullDownToRefresh(PullToRefreshBase refreshView) {
			if (mUpdateTask != null) {
				mUpdateTask.cancel(true);
			}
			mUpdateTask = new UpdateTask();
			mUpdateTask.execute(1);
		}

		/**
		 * 尾部拉动 载入更多
		 */
		@Override
		public void onPullUpToRefresh(PullToRefreshBase refreshView) {
			if (mMoreTask != null) {
				mMoreTask.cancel(true);
			}
			mMoreTask = new MoreTask();
			mMoreTask.execute(1);
		}
	}// end inner class

	private final class UpdateTask extends
			AsyncTask<Integer, Void, ColleagePage> {
		@Override
		protected ColleagePage doInBackground(Integer... args) {
			NetService netService = new NetService();
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("page_size", pageSize + ""));
			params.add(new BasicNameValuePair("ts", timeStamp + ""));
//			System.out.println("ts---->"+timeStamp);
			try {
				String originStr = netService.sendGet(requestUrl, params);
				return ColleagePage.instanceFromOrigin(originStr);
				// return ColleagePage
				// .instanceFromOrigin("{'response':{news:[{'id':'12','thumb_pic':'http://t1.baidu.com/it/u=2153192240,1555432903&fm=23&gp=0.jpg','type':'1','title':'无畏上将 高尔察克','publish_time':'2013-01-13','content_url':'http://www.sina.com','ts':'0'}]},error_code:'',error_msg:'操你妹'}");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ColleagePage result) {
			super.onPostExecute(result);
			mPullRefreshListView.onRefreshComplete();
			if (result == null) {
				Toast.makeText(getActivity(), R.string.net_error,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (result.getErrorCode() != null) {
				Toast.makeText(getActivity(), result.getErrorMsg(),
						Toast.LENGTH_SHORT).show();
				return;
			}
			// 更新时间戳
			if (result.getTimeStamp() != null){
				timeStamp = result.getTimeStamp();

				SharedPreferences sp = getActivity().getSharedPreferences(Config.PACKAGE, Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putLong(Constants.COLLEAGE_LAST_UPDATETIME, timeStamp);
				editor.commit();
			}
			LinkedList<ColleageMsg> addPicNewsList = result.getPicList();
			LinkedList<ColleageMsg> addNewsList = result.getNormalList();
			dataList.addAll(0, addPicNewsList);
			limitDataListNum();
			if(dataList.size()>0){
				if (dataList.get(0) != null) {
					newsGalleryText.setText(dataList.get(0).getTitle());
				}
			}
			mIndexView.setNum(dataList.size());
			normalNewsList.addAll(0, addNewsList);
			mPagerAdapter.notifyDataSetChanged();
			normalListAdapter.notifyDataSetChanged();
		}
	}// end inner class

	private final class MoreTask extends AsyncTask<Integer, Void, ColleagePage> {
		@Override
		protected ColleagePage doInBackground(Integer... args) {
			NetService netService = new NetService();
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("page_size", pageSize + ""));
			params.add(new BasicNameValuePair("page", page + ""));
			try {
				String originStr = netService.sendGet(requestUrl, params);
				return ColleagePage.instanceFromOrigin(originStr);
				// return ColleagePage
				// .instanceFromOrigin("{'response':{news:[{'id':'12','thumb_pic':'http://t1.baidu.com/it/u=2153192240,1555432903&fm=23&gp=0.jpg','type':'0','title':'无畏上将 高尔察克','publish_time':'2013-01-13','content_url':'http://www.sina.com','ts':'0'},{'id':'12','thumb_pic':'http://t3.baidu.com/it/u=816471944,1242431874&fm=90&gp=0.jpg','type':'0','title':'无畏上将 高尔察克','publish_time':'2013-01-13','content_url':'http://www.sina.com','ts':'0'}]},error_code:'',error_msg:'操你妹'}");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ColleagePage result) {
			super.onPostExecute(result);
			mPullRefreshListView.onRefreshComplete();
			if (result == null) {
				Toast.makeText(getActivity(), R.string.net_error,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (result.getErrorCode() != null) {
				Toast.makeText(getActivity(), result.getErrorMsg(),
						Toast.LENGTH_SHORT).show();
				return;
			}
			page++;// 载入更多数据成功 更新页数
			LinkedList<ColleageMsg> addPicNewsList = result.getPicList();
			LinkedList<ColleageMsg> addNewsList = result.getNormalList();
			dataList.addAll(dataList.size(), addPicNewsList);
			normalNewsList.addAll(normalNewsList.size(), addNewsList);
			limitDataListNum();
			mIndexView.setNum(dataList.size());
			mPagerAdapter.notifyDataSetChanged();
			normalListAdapter.notifyDataSetChanged();
		}
	}// end inner class

	private final class ImagePagerAdapter extends FragmentStatePagerAdapter {
		public ImagePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Fragment getItem(int position) {
			return ImageDetailColleageFragment.newInstance(dataList
					.get(position));
		}
	}// end inner class
}// end class
