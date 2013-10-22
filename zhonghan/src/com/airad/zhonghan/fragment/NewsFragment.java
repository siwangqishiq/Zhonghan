package com.airad.zhonghan.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;

import com.airad.zhonghan.Config;
import com.airad.zhonghan.MainActivity;
import com.airad.zhonghan.R;
import com.airad.zhonghan.business.NetService;
import com.airad.zhonghan.data.Constants;
import com.airad.zhonghan.factory.ImageFetcherFactory;
import com.airad.zhonghan.model.Magzine;
import com.airad.zhonghan.model.News;
import com.airad.zhonghan.model.NewsPage;
import com.airad.zhonghan.model.PicNews;
import com.airad.zhonghan.ui.IndexView;
import com.airad.zhonghan.ui.components.ImageFetcher;
import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 新闻汇总页
 * 
 * @author panyi
 * 
 */
public class NewsFragment extends Fragment {
	public static final String TAG = "tab_news";
	public String requestUrl = Config.host + "/api/zhonghan/news";

	private ImageFetcher mImageFetcher;
	private LinkedList<String> mListItems;
	private LinkedList<News> dataList;
	private LinkedList<News> normalNewsList;
	private ImagePagerAdapter mPagerAdapter;
	private View view;
	private MainActivity mMainActivity;
	private ViewPager newsGallery;
	private TextView newsGalleryText;
	private NormalNewsListAdapter normalListAdapter;
	private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;
	private NewsDetail newsDetail;
	private GetDataTask mGetDataTask;
	private long timeStamp;
	private int page = 1;
	public static final int pageSize = 10;
	private LoadNewTask mLoadNewTask;
	private LoadMoreTask mLoadMoreTask;
	private IndexView mIndexView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.newsfrg, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initComponent();
		if (mGetDataTask != null) {
			mGetDataTask.cancel(true);
		}
		mGetDataTask = new GetDataTask();
		mGetDataTask.execute(1);// 启动访问网络获取数据
	}

	private void initComponent() {
		mMainActivity = (MainActivity) getActivity();
		mImageFetcher = ImageFetcherFactory.genImageFetcher(getActivity(),
				R.drawable.ic_action_search);
		mPullRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setMode(Mode.BOTH);// 设置上下都可以拉动
		actualListView = mPullRefreshListView.getRefreshableView();
		View galleryView = LayoutInflater.from(mMainActivity).inflate(
				R.layout.gallery, null);
		newsGallery = (ViewPager) galleryView.findViewById(R.id.gallery);
		mIndexView = (IndexView)galleryView.findViewById(R.id.indexView);
		newsGalleryText = (TextView) galleryView
				.findViewById(R.id.gallery_text);
		actualListView.addHeaderView(galleryView);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mGetDataTask != null) {
			mGetDataTask.cancel(true);
		}
		if (mLoadNewTask != null) {
			mLoadNewTask.cancel(true);
		}
		if (mLoadMoreTask != null) {
			mLoadMoreTask.cancel(true);
		}
	}

	private final class ListClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long index) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			if (fm.findFragmentByTag(NewsDetail.TAG) == null) {
				News news = normalNewsList.get((int) index);
				FragmentTransaction ft = fm.beginTransaction();
				if (newsDetail == null) {
					newsDetail = NewsDetail.newInstance(news.getWebUrl());
				} else {
					newsDetail.loadUrl(news.getWebUrl());
				}

				ImageView isNew = (ImageView) view
						.findViewById(R.id.is_new_flag);
				isNew.setVisibility(View.INVISIBLE);
				news.setTs(0L);
				SharedPreferences sp = getActivity().getSharedPreferences(
						Config.PACKAGE, Context.MODE_PRIVATE);
				String newsMap = sp.getString(Constants.NEWS_MAP, "");
				if (newsMap.indexOf(news.getId()) == -1) {
					newsMap += ("#" + news.getId());
					sp.edit().putString(Constants.NEWS_MAP, newsMap).commit();
				}

				ft.add(R.id.add_container, newsDetail, NewsDetail.TAG);
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
				convertView = mInflater
						.inflate(R.layout.normal_news_item, null);
			}
			ImageView img = (ImageView) convertView.findViewById(R.id.news_pic);
			ImageView isnew = (ImageView) convertView
					.findViewById(R.id.is_new_flag);
			isnew.setVisibility(View.INVISIBLE);
			TextView title = (TextView) convertView
					.findViewById(R.id.news_title);
			TextView date = (TextView) convertView.findViewById(R.id.news_date);
			News item = normalNewsList.get(position);
			if (System.currentTimeMillis() - (item.getTs() * 1000) <= Constants.DAY_DELTA
					&& !isHasVisitNews(item.getId())) {
				isnew.setVisibility(View.VISIBLE);
			}
			mImageFetcher.loadImage(item.getPicUrl(), img);
			title.setText(item.getTitle());
			date.setText(item.getDate());
			return convertView;
		}
	}// end inner class

	private boolean isHasVisitNews(String id) {
		String newsMap = getActivity().getSharedPreferences(Config.PACKAGE,
				Context.MODE_PRIVATE).getString(Constants.NEWS_MAP, "");
		if (newsMap.indexOf(id) != -1) {
			return true;
		} else {
			return false;
		}
	}

	private final class GetDataTask extends AsyncTask<Integer, Void, NewsPage> {
		@Override
		protected NewsPage doInBackground(Integer... arg0) {
			NetService netService = new NetService();
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("page_size", pageSize + ""));
			params.add(new BasicNameValuePair("page", page + ""));
			try {
				String originStr = netService.sendGet(requestUrl, params);
				return NewsPage.instanceFromOrigin(originStr);
				// return NewsPage
				// .instanceFromOrigin("{'response':{news:[{'id':'12','thumb_pic':'http://t3.baidu.com/it/u=971069590,3779202590&fm=11&gp=0.jpg','type':'1','title':'Attack On Titan','publish_time':'2013-01-13','content_url':'http://www.sina.com','ts':'0'},{'id':'12','thumb_pic':'http://a.hiphotos.baidu.com/album/w%3D2048/sign=820cba16bb12c8fcb4f3f1cdc83b9345/ac4bd11373f08202aa28590949fbfbedab641b4a.jpg','type':'1','title':'kill all','publish_time':'2013-01-13','content_url':'http://www.qq.com','ts':'0'},{'id':'12','thumb_pic':'http://t2.baidu.com/it/u=2335953917,1817544965&fm=11&gp=0.jpg','type':'1','title':'你好 世界!','publish_time':'2013-01-  13','content_url':'http://www.baidu.com','ts':'0'},{'id':'12','thumb_pic':'http://t1.baidu.com/it/u=1237721610,1791710448&fm=11&gp=0.jpg','type':'0','title':'进击的巨人','publish_time':'2013-01-  13','content_url':'http://www.baidu.com','ts':'0'},{'id':'12','thumb_pic':'http://t1.baidu.com/it/u=1237721610,1791710448&fm=11&gp=0.jpg','type':'0','title':'进击的巨人','publish_time':'2013-01-  13','content_url':'http://www.baidu.com','ts':'0'},{'id':'12','thumb_pic':'http://t2.baidu.com/it/u=2361537521,2767569300&fm=11&gp=0.jpg','type':'0','title':'进击的巨人','publish_time':'2013-01-  13','content_url':'http://www.baidu.com','ts':'0'}]},error_code:'',error_msg:'操你妹'}");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(NewsPage result) {
			super.onPostExecute(result);
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
			dataList = result.getImageNews();
			limitDataListSize();
			if (dataList.size() > 0){
				if (dataList.get(0) != null) {
					newsGalleryText.setText(dataList.get(0).getTitle());
				}
			}
			normalNewsList = result.getNormalNews();

			page++;// 页数加1
			if (result.getTs() != null){
				timeStamp = result.getTs();// 更新时间戳
				
				SharedPreferences sp = getActivity().getSharedPreferences(Config.PACKAGE, Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putLong(Constants.NEWS_LAST_UPDATETIME, timeStamp);
				editor.commit();
			}
			
			
			
			mPagerAdapter = new ImagePagerAdapter(
					mMainActivity.getSupportFragmentManager());
			newsGallery.setAdapter(mPagerAdapter);
			normalListAdapter = new NormalNewsListAdapter();

			actualListView.setAdapter(normalListAdapter);
			actualListView.setOnItemClickListener(new ListClick());
			mIndexView.setNum(dataList.size());
			
			newsGallery.setOnPageChangeListener(new OnPageChangeListener() {
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
			mPullRefreshListView.setOnRefreshListener(new PullListener());
		}
	} // end inner class

	private final class PullListener implements OnRefreshListener2 {
		/**
		 * 头部拉动 更新新内容
		 */
		@Override
		public void onPullDownToRefresh(PullToRefreshBase refreshView) {
			if (mLoadNewTask != null) {
				mLoadNewTask.cancel(true);
			}
			mLoadNewTask = new LoadNewTask();
			mLoadNewTask.execute(1);
		}

		/**
		 * 尾部拉动 载入更多
		 */
		@Override
		public void onPullUpToRefresh(PullToRefreshBase refreshView) {
			if (mLoadMoreTask != null) {
				mLoadMoreTask.cancel(true);
			}
			mLoadMoreTask = new LoadMoreTask();
			mLoadMoreTask.execute(1);
		}
	}// end inner class

	/**
	 * 请求更新数据
	 * 
	 * @author Panyi
	 * 
	 */
	private final class LoadNewTask extends AsyncTask<Integer, Void, NewsPage> {
		@Override
		protected NewsPage doInBackground(Integer... args) {
			NetService netService = new NetService();
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("page_size", pageSize + ""));
			params.add(new BasicNameValuePair("ts", timeStamp + ""));
			// System.out.println("---->"+pageSize + ","+timeStamp);
			try {
				String originStr = netService.sendGet(requestUrl, params);
				return NewsPage.instanceFromOrigin(originStr);
				// return NewsPage
				// .instanceFromOrigin("{'response':{news:[{'id':'12','thumb_pic':'http://t1.baidu.com/it/u=2153192240,1555432903&fm=23&gp=0.jpg','type':'1','title':'无畏上将 高尔察克','publish_time':'2013-01-13','content_url':'http://www.sina.com','ts':'0'}]},error_code:'',error_msg:'操你妹'}");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(NewsPage result) {
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
			if (result.getTs() != null){
				timeStamp = result.getTs();
				
				SharedPreferences sp = getActivity().getSharedPreferences(Config.PACKAGE, Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putLong(Constants.NEWS_LAST_UPDATETIME, timeStamp);
				editor.commit();
			}
			LinkedList<News> addPicNewsList = result.getImageNews();
			LinkedList<News> addNewsList = result.getNormalNews();

			dataList.addAll(0, addPicNewsList);
			limitDataListSize();
			if(dataList.size()>0){
				if (dataList.get(0) != null) {
					newsGalleryText.setText(dataList.get(0).getTitle());
				}
			}
			mIndexView.setNum(dataList.size());
			normalNewsList.addAll(0, addNewsList);
			mPagerAdapter.notifyDataSetChanged();
			normalListAdapter.notifyDataSetChanged();

			super.onPostExecute(result);
		}
	}// end inner class

	private void limitDataListSize() {
		while (dataList.size() > 3) {
			dataList.removeLast();
		}// end while
	}

	private final class LoadMoreTask extends AsyncTask<Integer, Void, NewsPage> {
		@Override
		protected NewsPage doInBackground(Integer... args) {
			NetService netService = new NetService();
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("page_size", pageSize + ""));
			params.add(new BasicNameValuePair("page", page + ""));
			try {
				String originStr = netService.sendGet(requestUrl, params);
				return NewsPage.instanceFromOrigin(originStr);
				// return NewsPage
				// .instanceFromOrigin("{'response':{news:[{'id':'12','thumb_pic':'http://t1.baidu.com/it/u=2153192240,1555432903&fm=23&gp=0.jpg','type':'0','title':'无畏上将 高尔察克','publish_time':'2013-01-13','content_url':'http://www.sina.com','ts':'0'},{'id':'12','thumb_pic':'http://t3.baidu.com/it/u=816471944,1242431874&fm=90&gp=0.jpg','type':'0','title':'无畏上将 高尔察克','publish_time':'2013-01-13','content_url':'http://www.sina.com','ts':'0'}]},error_code:'',error_msg:'操你妹'}");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(NewsPage result) {
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
			LinkedList<News> addPicNewsList = result.getImageNews();
			LinkedList<News> addNewsList = result.getNormalNews();
			dataList.addAll(dataList.size(), addPicNewsList);
			normalNewsList.addAll(normalNewsList.size(), addNewsList);
			limitDataListSize();
			mIndexView.setNum(dataList.size());
			mPagerAdapter.notifyDataSetChanged();
			normalListAdapter.notifyDataSetChanged();

			super.onPostExecute(result);
		}
	}// end inner class

	private final class ImagePagerAdapter extends FragmentStatePagerAdapter {
		public ImagePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int position) {
			return ImageDetailFragment.newInstance(dataList.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup group, int index) {
			return super.instantiateItem(group, index);
		}
	}// end inner class

}// end class
