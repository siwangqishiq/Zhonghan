package com.airad.zhonghan.factory;

import com.airad.zhonghan.data.Config;
import com.airad.zhonghan.ui.components.ImageCache.ImageCacheParams;
import com.airad.zhonghan.ui.components.ImageFetcher;

import android.graphics.Bitmap.CompressFormat;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;

public class ImageFetcherFactory {
	public static ImageFetcher genImageFetcher(FragmentActivity context) {
		ImageCacheParams cacheParams = new ImageCacheParams(context,
				Config.IMAGE_CACHE_DIR);
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		int width = displayMetrics.widthPixels;
		final int longest = (height > width ? height : width) / 2;
		cacheParams.compressFormat = CompressFormat.JPEG;
		cacheParams.setMemCacheSizePercent(context, 0.15f);
		ImageFetcher mImageFetcher = new ImageFetcher(context, longest);
		mImageFetcher.addImageCache(context.getSupportFragmentManager(),
				cacheParams);
		return mImageFetcher;
	}
	
	public static ImageFetcher genImageFetcher(FragmentActivity context,int img) {
		ImageFetcher mImageFetcher = genImageFetcher(context);
		mImageFetcher.setLoadingImage(img);
		return mImageFetcher;
	}
}// end class
