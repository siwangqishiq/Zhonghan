package com.airad.zhonghan.model;

import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 新闻 大图新闻 普通新闻
 * 
 * @author Panyi
 * 
 */
public class NewsPage extends ErrorBase {
	private LinkedList<News> imageNews;
	private LinkedList<News> normalNews;
	private Long ts;

	public Long getTs() {
		return ts;
	}

	public void setTs(Long ts) {
		this.ts = ts;
	}

	public NewsPage() {
		imageNews = new LinkedList<News>();
		normalNews = new LinkedList<News>();
	}

	public LinkedList<News> getImageNews() {
		return imageNews;
	}

	public void setImageNews(LinkedList<News> imageNews) {
		this.imageNews = imageNews;
	}

	public LinkedList<News> getNormalNews() {
		return normalNews;
	}

	public void setNormalNews(LinkedList<News> normalNews) {
		this.normalNews = normalNews;
	}

	public static NewsPage instanceFromOrigin(String origin) {
		try {
			JSONObject obj = new JSONObject(origin);
			NewsPage ret = new NewsPage();
			if (setErrorBase(ret, obj)) {
				return ret;
			}
			JSONObject response = obj.getJSONObject("response");
			JSONArray array = response.getJSONArray("news");
			for (int i = 0, length = array.length(); i < length; i++) {
				JSONObject newsItem = array.getJSONObject(i);
				News item =new News();
				item.setId(newsItem.getString("id"));
				item.setPicUrl(newsItem.getString("thumb_pic"));
				item.setTitle(newsItem.getString("title"));
				item.setDate(newsItem.getString("publish_time"));
				item.setWebUrl(newsItem.getString("content_url"));
				item.setTs(newsItem.getLong("ts"));
				if(i==0){//设置时间戳
					ret.setTs(item.getTs());
				}
				//新闻类型判断
				int type = newsItem.getInt("type");
				if(News.LIST_TYPE==type){
					ret.getNormalNews().add(item);
				}else if(News.BANNER_TYPE == type){
					ret.getImageNews().add(item);
				}
			}// end for i
			return ret;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}// end class
