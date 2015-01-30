package com.frank.haomei.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * @author XuYongqi
 *
 */
public class WeatherUtil {
	
	public static final String DEGREE="℃";
	public static final String FORECAST_F="forecast_f";
	public static final String FORECAST_V="forecast_v";
	public static final String INDEX_F="index_f";
	public static final String INDEX_V="index_v";
	
	/**
	 * 密钥
	 */
	private static final String KEY="99fef4_SmartWeatherAPI_42d4938";	  
	
	private static final String URL_FORMAT="http://open.weather.com.cn/data/?areaid=%s&type=%s&date=%s&appid=4f033001ce3a1518";

	private static final char last2byte = (char) Integer.parseInt("00000011", 2);
	private static final char last4byte = (char) Integer.parseInt("00001111", 2);
	private static final char last6byte = (char) Integer.parseInt("00111111", 2);
	private static final char lead6byte = (char) Integer.parseInt("11111100", 2);
	private static final char lead4byte = (char) Integer.parseInt("11110000", 2);
	private static final char lead2byte = (char) Integer.parseInt("11000000", 2);
	private static final char[] encodeTable = new char[] { 'A', 'B', 'C', 'D',
			'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
			'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', '+', '/' 
	};

	public static String standardURLEncoder(String data, String key) {
		byte[] byteHMAC = null;
		String urlEncoder = "";
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
			mac.init(spec);
			byteHMAC = mac.doFinal(data.getBytes());
			if (byteHMAC != null) {
				String oauth = encode(byteHMAC);
				if (oauth != null) {
					urlEncoder = URLEncoder.encode(oauth, "utf8");
				}
			}
		} catch (InvalidKeyException e1) {
			e1.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return urlEncoder;
	}

	public static String encode(byte[] from) {
		StringBuffer to = new StringBuffer((int) (from.length * 1.34) + 3);
		int num = 0;
		char currentByte = 0;
		for (int i = 0; i < from.length; i++) {
			num = num % 8;
			while (num < 8) {
				switch (num) {
				case 0:
					currentByte = (char) (from[i] & lead6byte);
					currentByte = (char) (currentByte >>> 2);
					break;
				case 2:
					currentByte = (char) (from[i] & last6byte);
					break;
				case 4:
					currentByte = (char) (from[i] & last4byte);
					currentByte = (char) (currentByte << 2);
					if ((i + 1) < from.length) {
						currentByte |= (from[i + 1] & lead2byte) >>> 6;
					}
					break;
				case 6:
					currentByte = (char) (from[i] & last2byte);
					currentByte = (char) (currentByte << 4);
					if ((i + 1) < from.length) {
						currentByte |= (from[i + 1] & lead4byte) >>> 4;
					}
					break;
				}
				to.append(encodeTable[currentByte]);
				num += 6;
			}
		}
		if (to.length() % 4 != 0) {
			for (int i = 4 - to.length() % 4; i > 0; i--) {
				to.append("=");
			}
		}
		return to.toString();
	}
	
	public static String getUrl(String areaId,String type){
		SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMddhhmm",Locale.getDefault());
        String urlString=String.format(URL_FORMAT, areaId,type,dateFm.format(new Date()));
        String str = standardURLEncoder(urlString, KEY);
        return urlString.substring(0, urlString.length()-10)+"&key="+str;
	}
	
	public static void main(String[] args) {
		try {
			
			System.out.println(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
			 Date date=new Date();
			   SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
			   System.out.println(dateFm.format(date));
			
			//需要加密的数据  
            String data = "http://open.weather.com.cn/data/?areaid=101010100&type=forecast_f&date=201501201935&appid=4f033001ce3a1518";  
            //密钥  
            String key = "99fef4_SmartWeatherAPI_42d4938";  
            
            String str = standardURLEncoder(data, key);
            
            String urlString=data.substring(0, data.length()-10)+"&key="+str;

            System.out.println(urlString);
            
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
	            System.out.println(strBuffer.toString());
	        } catch (Exception e) {
	        	
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}