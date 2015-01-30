package com.frank.haomei.base;

import android.app.Application;
import android.content.Context;

public class SysApplication extends Application {
	private static Context context;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context=this.getApplicationContext();
	}
	
	public static Context getContext(){
		return context;
	}
}
