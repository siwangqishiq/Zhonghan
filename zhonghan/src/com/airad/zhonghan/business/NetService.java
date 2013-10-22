package com.airad.zhonghan.business;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;

/**
 * 网络请求控件
 * 
 * @author Panyi
 * 
 */
public class NetService {
	private String appCode;
	protected HttpClient httpClient;
	public static final String MT_APPCODE = "mt-appcode";
	
	public NetService() {
		httpClient = new DefaultHttpClient();
		appCode = "";
	}

	public String sendGet(String url, List<BasicNameValuePair> params)
			throws ParseException, UnsupportedEncodingException, IOException,
			URISyntaxException {
		String body = null;
		// Get请求
		HttpGet httpget = new HttpGet(url);
		// 设置参数
		String str = EntityUtils.toString(new UrlEncodedFormEntity(params));
		httpget.setURI(new URI(httpget.getURI().toString() + "?" + str));
		// 发送请求
		HttpResponse httpresponse = httpClient.execute(httpget);
		// 获取返回数据
		HttpEntity entity = httpresponse.getEntity();
		body = EntityUtils.toString(entity);
		if (entity != null) {
			entity.consumeContent();
		}
		return body;
	}
}// end class

