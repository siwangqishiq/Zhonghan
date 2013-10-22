package com.airad.zhonghan.model;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MagPage extends ErrorBase {
	private LinkedList<Magzine> dataList;
	private Long timeStamp;
	
	public MagPage(){
		dataList = new LinkedList<Magzine>();
	}
	public LinkedList<Magzine> getDataList() {
		return dataList;
	}

	public void setDataList(LinkedList<Magzine> dataList) {
		this.dataList = dataList;
	}

	public Long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public static MagPage instanceFromOrigin(String origin) {
		try {
			JSONObject obj = new JSONObject(origin);
			MagPage ret = new MagPage();
			if (setErrorBase(ret, obj)) {
				return ret;
			}
			JSONObject response = obj.getJSONObject("response");
			JSONArray array = response.getJSONArray("emags");
			for (int i = 0, length = array.length(); i < length; i++) {
				JSONObject magzineItem = array.getJSONObject(i);
				Magzine item =new Magzine();
				item.setId(magzineItem.getInt("id"));
				item.setTitle(magzineItem.getString("title"));
				item.setPic(magzineItem.getString("thumb_pic"));
				item.setUpdateTime(magzineItem.getString("publish_time"));
				item.setTs(magzineItem.getLong("ts"));
				item.setSummary(magzineItem.getString("desc"));
				item.setWebUrl(magzineItem.getString("content_url"));
				if(i==0){//设置时间戳
					ret.setTimeStamp(item.getTs());
				}
				ret.getDataList().add(item);
			}// end for i
			return ret;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}//end class
