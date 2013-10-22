package com.airad.zhonghan.fragment;

import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.http.message.BasicNameValuePair;

import com.airad.zhonghan.Config;
import com.airad.zhonghan.R;
import com.airad.zhonghan.business.NetService;
import com.airad.zhonghan.data.Constants;
import com.airad.zhonghan.factory.ImageFetcherFactory;
import com.airad.zhonghan.model.MagPage;
import com.airad.zhonghan.model.Magzine;
import com.airad.zhonghan.model.News;
import com.airad.zhonghan.model.NewsPage;
import com.airad.zhonghan.ui.components.ImageFetcher;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

public class MagazineFragment extends Fragment {
	public static final String TAG = "tab_magazine";

	public static final int pageSize = 10;
	public String requestUrl = Config.host + "/api/zhonghan/emags";
	private View mainView;
	private ImageFetcher mImageFetcher;
	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
	private LinkedList<Magzine> dataList;
	private ListAdapter listAdapter;
	private MagazineDetail magazineDetail;
	private GetDataTask mGetDataTask;
	private long timeStamp = 0;
	private int page = 1;
	private UpdateTask mUpdateTask;
	private MoreTask mMoreTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.magazinefrg, container, false);
		return mainView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mImageFetcher = ImageFetcherFactory.genImageFetcher(getActivity(),
				R.drawable.ic_action_search);

		mPullRefreshListView = (PullToRefreshListView) mainView
				.findViewById(R.id.magzine_list);
		mPullRefreshListView.setMode(Mode.BOTH);
		mListView = mPullRefreshListView.getRefreshableView();

		if (mGetDataTask != null) {
			mGetDataTask.cancel(true);
		}
		mGetDataTask = new GetDataTask();
		mGetDataTask.execute(1);
	}

	private final class GetDataTask extends AsyncTask<Integer, Void, MagPage> {
		@Override
		protected MagPage doInBackground(Integer... args) {
			NetService netService = new NetService();
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("page_size", pageSize + ""));
			params.add(new BasicNameValuePair("page", page + ""));
			try {
				String originStr = netService.sendGet(requestUrl, params);
				return MagPage.instanceFromOrigin(originStr);
				// return MagPage
				// .instanceFromOrigin("{response:{emags:[{id:'1',title:'特征值',thumb_pic:'http://imgstatic.baidu.com/img/image/shouye/1111111.jpg',publish_time:'2013-01-01',desc:'爸爸又干了半杯酒，然后转向我，“其实，儿子，过一个美妙的人生并不难，听爸爸教你：你选一个公认的世界难题，最好是只用一张纸和一只铅笔的数学难题，比如歌德巴赫猜想或费尔马大定理什么的，或连纸笔都不要的纯自然哲学难题，比如宇宙的本源之类，投入全部身心钻研，只问耕耘不问收获，不知不觉的专注中，一辈子也就过去了。人们常说的寄托，也就是这么回事。或是相反，把挣钱作为惟一的目标，所有的时间都想着怎么挣，也不问挣来干什么用，到死的时候像葛朗台一样抱者一堆金币说：啊，真暖和啊……所以，美妙人生的关键在于你能迷上什么东西。比如我——”爸爸指指房间里到处摆放着的那些小幅水彩画，它们的技法都很传统，画得中规中矩，从中看不出什么灵气来。这些画映着窗外的电光，像一群闪动的屏幕，“我迷上了画画，虽然知道自己成不了梵高。”',content_url:'http://www.qq.com',ts:0},{id:'1',title:'特征值',thumb_pic:'http://d.hiphotos.baidu.com/image/q%3D100%3Ba0%3D+%2C1%2C1/sign=e84a1ee517ce36d3a40487300ac85bb7/e61190ef76c6a7ef7de44a19fffaaf51f2de66cb.jpg',publish_time:'2013-01-01',desc:'爸爸又干了半杯酒，然后转向我，“其实，儿子，过一个美妙的人生并不难，听爸爸教你：你选一个公认的世界难题，最好是只用一张纸和一只铅笔的数学难题，比如歌德巴赫猜想或费尔马大定理什么的，或连纸笔都不要的纯自然哲学难题，比如宇宙的本源之类，投入全部身心钻研，只问耕耘不问收获，不知不觉的专注中，一辈子也就过去了。人们常说的寄托，也就是这么回事。或是相反，把挣钱作为惟一的目标，所有的时间都想着怎么挣，也不问挣来干什么用，到死的时候像葛朗台一样抱者一堆金币说：啊，真暖和啊……所以，美妙人生的关键在于你能迷上什么东西。比如我——”爸爸指指房间里到处摆放着的那些小幅水彩画，它们的技法都很传统，画得中规中矩，从中看不出什么灵气来。这些画映着窗外的电光，像一群闪动的屏幕，“我迷上了画画，虽然知道自己成不了梵高。”',content_url:'http://www.qq.com',ts:0},{id:'1',title:'特征值',thumb_pic:'http://c.hiphotos.baidu.com/pic/w%3D230/sign=2ae326a8aec379317d68812adbc5b784/0b7b02087bf40ad130ca234d562c11dfa9ecce76.jpg',publish_time:'2013-01-01',desc:'爸爸又干了半杯酒，然后转向我，“其实，儿子，过一个美妙的人生并不难，听爸爸教你：你选一个公认的世界难题，最好是只用一张纸和一只铅笔的数学难题，比如歌德巴赫猜想或费尔马大定理什么的，或连纸笔都不要的纯自然哲学难题，比如宇宙的本源之类，投入全部身心钻研，只问耕耘不问收获，不知不觉的专注中，一辈子也就过去了。人们常说的寄托，也就是这么回事。或是相反，把挣钱作为惟一的目标，所有的时间都想着怎么挣，也不问挣来干什么用，到死的时候像葛朗台一样抱者一堆金币说：啊，真暖和啊……所以，美妙人生的关键在于你能迷上什么东西。比如我——”爸爸指指房间里到处摆放着的那些小幅水彩画，它们的技法都很传统，画得中规中矩，从中看不出什么灵气来。这些画映着窗外的电光，像一群闪动的屏幕，“我迷上了画画，虽然知道自己成不了梵高。”',content_url:'http://www.qq.com',ts:0},{id:'1',title:'特征值',thumb_pic:'http://b.hiphotos.baidu.com/pic/w%3D230/sign=49b0ef99bba1cd1105b675238913c8b0/d01373f082025aaf15637562faedab64024f1ac1.jpg',publish_time:'2013-01-01',desc:'爸爸又干了半杯酒，然后转向我，“其实，儿子，过一个美妙的人生并不难，听爸爸教你：你选一个公认的世界难题，最好是只用一张纸和一只铅笔的数学难题，比如歌德巴赫猜想或费尔马大定理什么的，或连纸笔都不要的纯自然哲学难题，比如宇宙的本源之类，投入全部身心钻研，只问耕耘不问收获，不知不觉的专注中，一辈子也就过去了。人们常说的寄托，也就是这么回事。或是相反，把挣钱作为惟一的目标，所有的时间都想着怎么挣，也不问挣来干什么用，到死的时候像葛朗台一样抱者一堆金币说：啊，真暖和啊……所以，美妙人生的关键在于你能迷上什么东西。比如我——”爸爸指指房间里到处摆放着的那些小幅水彩画，它们的技法都很传统，画得中规中矩，从中看不出什么灵气来。这些画映着窗外的电光，像一群闪动的屏幕，“我迷上了画画，虽然知道自己成不了梵高。”',content_url:'http://www.qq.com',ts:0}]},error_code:'',error_msg:''}");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MagPage result) {
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
			dataList = result.getDataList();

			// 填充控件
			listAdapter = new ListAdapter();
			mListView.setAdapter(listAdapter);
			mListView.setOnItemClickListener(new ListClick());

			page++;// 页数自动+1
			if (result.getTimeStamp() != null)// 设置时间戳
			{
				timeStamp = result.getTimeStamp();
				
				SharedPreferences sp = getActivity().getSharedPreferences(Config.PACKAGE, Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putLong(Constants.MAGZINE_LAST_UPDATETIME, timeStamp);
				editor.commit();
			}
			// 注册上拉 下拉事件响应
			mPullRefreshListView.setOnRefreshListener(new PullListener());
		}
	}// end inner class

	private final class PullListener implements OnRefreshListener2<ListView> {
		/**
		 * 头部拉动 更新新内容
		 */
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			if (mUpdateTask != null) {
				mUpdateTask.cancel(true);
			}
			mUpdateTask = new UpdateTask();
			mUpdateTask.execute(1);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			if (mMoreTask != null) {
				mMoreTask.cancel(true);
			}
			mMoreTask = new MoreTask();
			mMoreTask.execute(1);
		}
	} // end inner class

	/**
	 * 更新新数据线程
	 * 
	 * @author Panyi
	 * 
	 */
	private final class UpdateTask extends AsyncTask<Integer, Void, MagPage> {
		@Override
		protected MagPage doInBackground(Integer... args) {
			NetService netService = new NetService();
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("page_size", pageSize + ""));
			params.add(new BasicNameValuePair("ts", timeStamp + ""));
			try {
				String originStr = netService.sendGet(requestUrl, params);
				return MagPage.instanceFromOrigin(originStr);
				// return MagPage
				// .instanceFromOrigin("{response:{emags:[{id:'1',title:'特征值',thumb_pic:'http://e.hiphotos.baidu.com/pic/w%3D230/sign=546f6c5cd52a283443a631086bb4c92e/00e93901213fb80e4fda1d9637d12f2eb9389463.jpg',publish_time:'2013-01-01',desc:'爸爸又干了半杯酒，然后转向我，“其实，儿子，过一个美妙的人生并不难，听爸爸教你：你选一个公认的世界难题，最好是只用一张纸和一只铅笔的数学难题，比如歌德巴赫猜想或费尔马大定理什么的，或连纸笔都不要的纯自然哲学难题，比如宇宙的本源之类，投入全部身心钻研，只问耕耘不问收获，不知不觉的专注中，一辈子也就过去了。人们常说的寄托，也就是这么回事。或是相反，把挣钱作为惟一的目标，所有的时间都想着怎么挣，也不问挣来干什么用，到死的时候像葛朗台一样抱者一堆金币说：啊，真暖和啊……所以，美妙人生的关键在于你能迷上什么东西。比如我——”爸爸指指房间里到处摆放着的那些小幅水彩画，它们的技法都很传统，画得中规中矩，从中看不出什么灵气来。这些画映着窗外的电光，像一群闪动的屏幕，“我迷上了画画，虽然知道自己成不了梵高。”',content_url:'http://www.qq.com',ts:0}]},error_code:'',error_msg:''}");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MagPage result) {
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
				editor.putLong(Constants.MAGZINE_LAST_UPDATETIME, timeStamp);
				editor.commit();
			}
			LinkedList<Magzine> addList = result.getDataList();
			dataList.addAll(0, addList);
			listAdapter.notifyDataSetChanged();
		}
	}// end inner class

	/**
	 * 查看更多线程
	 * 
	 * @author Panyi
	 * 
	 */
	private final class MoreTask extends AsyncTask<Integer, Void, MagPage> {
		@Override
		protected MagPage doInBackground(Integer... args) {
			NetService netService = new NetService();
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("page_size", pageSize + ""));
			params.add(new BasicNameValuePair("page", page + ""));
			try {
				String originStr = netService.sendGet(requestUrl, params);
				return MagPage.instanceFromOrigin(originStr);
				// return MagPage
				// .instanceFromOrigin("{response:{emags:[{id:'1',title:'特征值',thumb_pic:'http://e.hiphotos.baidu.com/pic/w%3D230/sign=546f6c5cd52a283443a631086bb4c92e/00e93901213fb80e4fda1d9637d12f2eb9389463.jpg',publish_time:'2013-01-01',desc:'爸爸又干了半杯酒，然后转向我，“其实，儿子，过一个美妙的人生并不难，听爸爸教你：你选一个公认的世界难题，最好是只用一张纸和一只铅笔的数学难题，比如歌德巴赫猜想或费尔马大定理什么的，或连纸笔都不要的纯自然哲学难题，比如宇宙的本源之类，投入全部身心钻研，只问耕耘不问收获，不知不觉的专注中，一辈子也就过去了。人们常说的寄托，也就是这么回事。或是相反，把挣钱作为惟一的目标，所有的时间都想着怎么挣，也不问挣来干什么用，到死的时候像葛朗台一样抱者一堆金币说：啊，真暖和啊……所以，美妙人生的关键在于你能迷上什么东西。比如我——”爸爸指指房间里到处摆放着的那些小幅水彩画，它们的技法都很传统，画得中规中矩，从中看不出什么灵气来。这些画映着窗外的电光，像一群闪动的屏幕，“我迷上了画画，虽然知道自己成不了梵高。”',content_url:'http://www.qq.com',ts:0}]},error_code:'',error_msg:''}");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MagPage result) {
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
			LinkedList<Magzine> addList = result.getDataList();
			dataList.addAll(dataList.size(), addList);
			listAdapter.notifyDataSetChanged();
		}
	}// end inner class

	private final class ListClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long index) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			if (fm.findFragmentByTag(MagazineDetail.TAG) == null) {
				Magzine magazine = dataList.get((int) index);
				FragmentTransaction ft = fm.beginTransaction();
				Fragment frg = MagazineDetail.newInstance(magazine);

				TextView title = (TextView) view.findViewById(R.id.title);
				title.setCompoundDrawables(null, null, null, null);
				magazine.setTs(0L);
				SharedPreferences sp = getActivity().getSharedPreferences(
						Config.PACKAGE, Context.MODE_PRIVATE);
				String magzineMap = sp.getString(Constants.MAGZINE_MAP, "");
				if (magzineMap.indexOf(magazine.getId()) == -1) {
					magzineMap += ("#" + magazine.getId());
					sp.edit().putString(Constants.MAGZINE_MAP, magzineMap)
							.commit();
				}

				ft.add(R.id.magazine_add, frg, MagazineDetail.TAG);
				ft.addToBackStack(TAG);
				ft.commit();
			} // end if
		}
	}// end inner class

	private final class ListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Drawable newDrawable;

		public ListAdapter() {
			mInflater = LayoutInflater.from(getActivity());
			newDrawable = getActivity().getResources().getDrawable(
					R.drawable.news_is_new);
			newDrawable.setBounds(0, 0, 50, 20);
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return (long) position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.magazine_item, null);
			}

			ImageView img = (ImageView) convertView.findViewById(R.id.pic);
			TextView title = (TextView) convertView.findViewById(R.id.title);
			title.setCompoundDrawables(null, null, null, null);
			TextView summary = (TextView) convertView
					.findViewById(R.id.summary);
			TextView date = (TextView) convertView.findViewById(R.id.date);

			Magzine data = dataList.get(position);
			mImageFetcher.loadImage(data.getPic(), img);
			title.setText(data.getTitle());
			date.setText(data.getUpdateTime());
			summary.setText(data.getSummary());

			if ((System.currentTimeMillis() - (data.getTs() * 1000) <= Constants.DAY_DELTA)
					&& !isHasVisitMagzine(data.getId() + "")) {
				title.setCompoundDrawables(null, null, newDrawable, null);
			}

			return convertView;
		}
	}// end inner class

	private boolean isHasVisitMagzine(String id) {
		String magzineMap = getActivity().getSharedPreferences(Config.PACKAGE,
				Context.MODE_PRIVATE).getString(Constants.MAGZINE_MAP, "");
		if (magzineMap.indexOf(id) != -1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mGetDataTask != null) {
			mGetDataTask.cancel(true);
		}
		if (mUpdateTask != null) {
			mUpdateTask.cancel(true);
		}
		if (mMoreTask != null) {
			mMoreTask.cancel(true);
		}
	}
}// end class
