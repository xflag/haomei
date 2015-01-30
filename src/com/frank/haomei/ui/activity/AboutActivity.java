package com.frank.haomei.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.frank.haomei.R;
import com.frank.haomei.base.BaseActivity;
import com.frank.haomei.util.LogUtil;

public class AboutActivity extends BaseActivity implements OnClickListener {
	private ImageView imgBack;
	private TextView textViewVersion;

	public static void startActivity(Activity activity) {
		Intent intent = new Intent(activity, AboutActivity.class);
		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		this.findViews();
		try {
			String pkName = this.getPackageName();
			String versionName = "V"+this.getPackageManager().getPackageInfo(pkName, 0).versionName;
			this.textViewVersion.setText(versionName);
		} catch (Exception e) {
			LogUtil.e(AboutActivity.class.getSimpleName(), e.getMessage());
		}
		
	}

	private void findViews() {
		this.imgBack = (ImageView) this.findViewById(R.id.imageViewBack);
		this.imgBack.setOnClickListener(this);
		this.textViewVersion = (TextView) this
				.findViewById(R.id.textViewVersion);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageViewBack:
			this.finish();
			break;
		default:
			break;
		}
	}
}
