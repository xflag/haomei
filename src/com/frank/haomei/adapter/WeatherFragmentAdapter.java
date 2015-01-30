package com.frank.haomei.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author XuYongqi
 *
 */
public class WeatherFragmentAdapter extends FragmentPagerAdapter {
	
	private List<Fragment> mFragments;  
	
	public WeatherFragmentAdapter(FragmentManager fm,List<Fragment> list) {  
        super(fm);  
        this.mFragments = list;            
    }

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.mFragments.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.mFragments.size();
	}  

	public void addItem(Fragment fragment){
		this.mFragments.add(fragment);
	}
	
	public void removeItem(int idx){
		this.mFragments.remove(idx);
	}

}
