package com.airad.zhonghan.fragment;

import com.airad.zhonghan.R;
import com.airad.zhonghan.factory.ImageFetcherFactory;
import com.airad.zhonghan.model.Magzine;
import com.airad.zhonghan.model.News;
import com.airad.zhonghan.ui.components.ImageFetcher;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MagazineDetail extends Fragment {
	public static final String TAG = "magazine_detail";
	private View mainView;
	private View backBtn;

	private Magzine data;
	private ImageView img;
	private TextView title,date,content;
	private ImageFetcher mImageFetcher;
	private Button readBtn;

	public static MagazineDetail newInstance(Magzine magzine) {
		MagazineDetail fragment = new MagazineDetail();
		fragment.data = magzine;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.magzine_detail, container, false);
		mImageFetcher = ImageFetcherFactory.genImageFetcher(getActivity(),
				R.drawable.ic_action_search);
		return mainView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		backBtn = mainView.findViewById(R.id.back_btn);
		backBtn.setOnClickListener(new BackOnClick());
		img = (ImageView)mainView.findViewById(R.id.pic);
		title = (TextView)mainView.findViewById(R.id.title);
		date= (TextView)mainView.findViewById(R.id.date);
		content= (TextView)mainView.findViewById(R.id.content);
		readBtn = (Button)mainView.findViewById(R.id.readBtn);
		
		if(data!=null){
			readBtn.setOnClickListener(new ReadBtn());
			mImageFetcher.loadImage(data.getPic(), img);
			title.setText(data.getTitle());
			date.setText(data.getUpdateTime());
			content.setText(data.getSummary());
		}
	}
	
	private final class ReadBtn implements OnClickListener{
		@Override
		public void onClick(View v) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			if(fm.findFragmentByTag(ReadMagazine.TAG) == null){
				String url  = data.getWebUrl();
				FragmentTransaction ft = fm.beginTransaction();
				ReadMagazine fragment = ReadMagazine.newInstance(url);
				ft.add(R.id.magazine_add, fragment, ReadMagazine.TAG);
				ft.addToBackStack(TAG);
				ft.commit();
			} //end if
		}
	}//end inner class
	

	private final class BackOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			fm.popBackStack(MagazineFragment.TAG, 1);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}// end class
