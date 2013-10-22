package com.airad.zhonghan.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.airad.zhonghan.utils.StringUtil;

public class ErrorBase {
	protected String errorCode;
	protected String errorMsg;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * true表示存在errorCode  没有正常数据
	 * false表示
	 * @param base
	 * @param obj
	 * @return
	 * @throws JSONException
	 */
	public static boolean setErrorBase(ErrorBase base, JSONObject obj)
			throws JSONException {
		if (StringUtil.isNotBlank(obj.getString("error_code"))) {// error_code不为空
																	// 网络有问题
			base.setErrorCode(obj.getString("error_code"));
			base.setErrorMsg(obj.getString("error_msg"));
			return true;
		}
		return false;
	}
}// end class
