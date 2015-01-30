package com.frank.haomei.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * @author XuYongqi
 *
 */
public class HttpUtil {
	public static void executeGet(final String urlString,final HttpCallbackListener listener) {
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				URL url = null;
		        HttpURLConnection connection = null;
		        InputStreamReader in = null;
		        try {
		            url = new URL(urlString);
		            connection = (HttpURLConnection) url.openConnection();
		            in = new InputStreamReader(connection.getInputStream(),"utf-8");
		            BufferedReader bufferedReader = new BufferedReader(in);
		            StringBuffer strBuffer = new StringBuffer();
		            String line = null;
		            while ((line = bufferedReader.readLine()) != null) {
		                strBuffer.append(line);
		            }
		            if(listener!=null)
		            	listener.onFinish(strBuffer.toString());
		        } catch (Exception e) {
		        	if(listener!=null)
		        		listener.onError(e);
		        } finally {
		            if (connection != null) {
		                connection.disconnect();
		            }
		            if (in != null) {
		                try {
		                    in.close();
		                } catch (IOException e) {
		                    LogUtil.e(HttpUtil.class.getSimpleName(), e.getMessage());
		                }
		            }		 
		        }
			}
		}).start();		
    }
	
	public static String executeHttpPost() {
        String result = null;
        URL url = null;
        HttpURLConnection connection = null;
        InputStreamReader in = null;
        try {
            url = new URL("http://10.0.2.2:8888/data/post/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "utf-8");
            DataOutputStream dop = new DataOutputStream(
                    connection.getOutputStream());
            dop.writeBytes("token=alexzhou");
            dop.flush();
            dop.close();
 
            in = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuffer strBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
 
        }
        return result;
    }
}