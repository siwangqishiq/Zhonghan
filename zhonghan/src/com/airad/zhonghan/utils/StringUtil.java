package com.airad.zhonghan.utils;

public class StringUtil {
	public static boolean isBlank(String str) {
		if (null == str || "".equals(str)) {
			return true;
		}
		return false;
	}
	
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
}
