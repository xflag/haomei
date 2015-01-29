package com.haomei.app.ui.wiget;

import com.haomei.app.util.LogUtil;

import android.R.integer;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 用于计算多行的textview的高度时使用
 * @author XuYongqi
 *
 */
public class HmGridTextView extends TextView {
	private Context context;

	public HmGridTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		Layout layout = getLayout();
		if (layout != null) {
			int height = (int) FloatMath.ceil(getMaxLineHeight(this.getText()
					.toString()))
					+ getCompoundPaddingTop()
					+ getCompoundPaddingBottom();
			int width = getMeasuredWidth();
			setMeasuredDimension(width, height);
		}
	}

	private float getMaxLineHeight(String str) {
		float height = 0.0f;
		float screenW = context.getResources().getDisplayMetrics().widthPixels;
		LinearLayout linearLayout=(LinearLayout)this.getParent();
//		linearLayout.measure(0, 0);
//		int w=linearLayout.getWidth();
		LayoutParams params=linearLayout.getLayoutParams();
		float paddingLeft = linearLayout.getPaddingLeft();
		float paddingReft = linearLayout.getPaddingRight();
		// 这里具体this.getPaint()要注意使用，要看你的TextView在什么位置，这个是拿TextView父控件的Padding的，为了更准确的算出换行
		int line = (int) Math.ceil((this.getPaint().measureText(str) / (screenW
				- paddingLeft - paddingReft)));
		height = (this.getPaint().getFontMetrics().descent - this.getPaint()
				.getFontMetrics().ascent) * line;
		LogUtil.i(HmGridTextView.class.getSimpleName(), screenW+"::"+paddingLeft+"::"+paddingReft+"::"+this.getPaint().measureText(str));
		LogUtil.i(HmGridTextView.class.getSimpleName(), line+"::"+height+"::"+str);
		return height;
	}

}
