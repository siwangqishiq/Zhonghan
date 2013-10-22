package com.airad.zhonghan.model;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ColleagePage extends ErrorBase{
	public static final int TYPE_PIC=1;
	public static final int TYPE_NORMAL=0;
	
	private LinkedList<ColleageMsg> normalList;
	private LinkedList<ColleageMsg> picList;
	private Long timeStamp;
	
	public ColleagePage(){
		normalList = new LinkedList<ColleageMsg>();
		picList = new LinkedList<ColleageMsg>();
	}
	
	public LinkedList<ColleageMsg> getNormalList() {
		return normalList;
	}
	public void setNormalList(LinkedList<ColleageMsg> normalList) {
		this.normalList = normalList;
	}
	public LinkedList<ColleageMsg> getPicList() {
		return picList;
	}
	public void setPicList(LinkedList<ColleageMsg> picList) {
		this.picList = picList;
	}
	public Long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	/**
	 * 
	 * @param origin
	 * @return
	 */
	public static ColleagePage instanceFromOrigin(String origin) {
		try {
			JSONObject obj = new JSONObject(origin);
			ColleagePage ret = new ColleagePage();
			if (setErrorBase(ret, obj)) {
				return ret;
			}
			JSONObject response = obj.getJSONObject("response");
			JSONArray array = response.getJSONArray("colleges");
			for (int i = 0, length = array.length(); i < length; i++) {
				JSONObject colleageItem = array.getJSONObject(i);
				ColleageMsg item =new ColleageMsg();
				item.setId(colleageItem.getInt("id"));
				item.setPic(colleageItem.getString("thumb_pic"));
				item.setTitle(colleageItem.getString("title"));
				item.setDate(colleageItem.getString("publish_time"));
				item.setWebUrl(colleageItem.getString("content_url"));
				item.setTimeStamp(colleageItem.getLong("ts"));
				if(i==0){//设置时间戳
					ret.setTimeStamp(item.getTimeStamp());
				}
				item.setType(colleageItem.getInt("category"));
				
				int type  = colleageItem.getInt("type");
				if(ColleagePage.TYPE_NORMAL == type){
					ret.getNormalList().add(item);
				}else if(ColleagePage.TYPE_PIC ==type){
					ret.getPicList().add(item);
				}
			}// end for i
			return ret;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}//end class
