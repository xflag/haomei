package com.haomei.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MainActivity extends Activity {
	PullToRefreshListView pullToRefreshView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		pullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
//		pullToRefreshView
//				.setOnRefreshListener(new OnRefreshListener<ListView>() {
//					@Override
//					public void onRefresh(
//							PullToRefreshBase<ListView> refreshView) {
//						// Do work to refresh the list here.
////						new GetDataTask().execute();
//					}
//				});
//
//	     class GetDataTask extends AsyncTask<Void, Void, String[]> {
//
//			@Override
//			protected void onPostExecute(String[] result) {
//				// Call onRefreshComplete when the list has been refreshed.
//				pullToRefreshView.onRefreshComplete();
//				super.onPostExecute(result);
//			}
//
//			@Override
//			protected String[] doInBackground(Void... arg0) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		}
	}
}
