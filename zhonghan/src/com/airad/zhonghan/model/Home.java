package com.airad.zhonghan.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Home extends ErrorBase {
	private Integer news;
	private Integer emag;
	private Integer colleage;
	public Integer getNews() {
		return news;
	}
	public void setNews(Integer news) {
		this.news = news;
	}
	public Integer getEmag() {
		return emag;
	}
	public void setEmag(Integer emag) {
		this.emag = emag;
	}
	public Integer getColleage() {
		return colleage;
	}
	public void setColleage(Integer colleage) {
		this.colleage = colleage;
	}
	
	public static Home instanceFromOrigin(String origin){
		Home ret = new Home();
		try {
			JSONObject obj = new JSONObject(origin);
			if(setErrorBase(ret, obj)){
				return ret;
			}
			//TODO
			JSONObject response = obj.getJSONObject("response");
			ret.setNews(response.getInt("news"));
			ret.setEmag(response.getInt("emag"));
			ret.setColleage(response.getInt("college"));
			return ret;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}//end class
