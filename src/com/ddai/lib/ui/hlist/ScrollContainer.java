package com.ddai.lib.ui.hlist;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ScrollContainer extends ScrollView {

	private OnScrollViewListener mOnScrollViewListener;

	public ScrollContainer(Context context) {
		super(context);
	}

	public ScrollContainer(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public ScrollContainer(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (this.mOnScrollViewListener != null) {
			this.mOnScrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public void setOnScrollViewListener(
			OnScrollViewListener paramOnScrollViewListener) {
		this.mOnScrollViewListener = paramOnScrollViewListener;
	}

	public static abstract interface OnScrollViewListener {
		public abstract void onScrollChanged(
				ScrollContainer paramYoukuScrollView, int paramInt1,
				int paramInt2, int paramInt3, int paramInt4);
	}
}
