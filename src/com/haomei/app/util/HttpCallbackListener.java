package com.haomei.app.util;

/**
 * 
 * @author XuYongqi
 *
 */
public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception exception);
}
