package com.airad.zhonghan.fragment;


import com.airad.zhonghan.MainActivity;
import com.airad.zhonghan.R;
import com.airad.zhonghan.model.News;
import com.airad.zhonghan.ui.components.ImageFetcher;
import com.airad.zhonghan.ui.components.ImageWorker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class ImageDetailFragment extends Fragment {
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private News news;
    private ImageView mImageView;
    private ImageFetcher mImageFetcher;

    public static ImageDetailFragment newInstance(News news) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        f.news = news;
        f.setArguments(args);
        return f;
    }

    public ImageDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				if(fm.findFragmentByTag(NewsDetail.TAG) == null){
					FragmentTransaction ft = fm.beginTransaction();
					NewsDetail newsDetail = NewsDetail.newInstance(news.getWebUrl());
					ft.add(R.id.add_container, newsDetail, NewsDetail.TAG);
					ft.addToBackStack(NewsFragment.TAG);
					ft.commit();
				} //end if
			}
		});
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (MainActivity.class.isInstance(getActivity())) {
            mImageFetcher = ((MainActivity) getActivity()).mImageFetcher;
            mImageFetcher.loadImage(news.getPicUrl(), mImageView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            ImageWorker.cancelWork(mImageView);
            mImageView.setImageDrawable(null);
        }
    }
}//end class
