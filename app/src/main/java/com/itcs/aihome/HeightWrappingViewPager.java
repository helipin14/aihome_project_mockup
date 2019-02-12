package com.itcs.aihome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class HeightWrappingViewPager extends ViewPager {
    public HeightWrappingViewPager(@NonNull Context context) {
        super(context);
    }

    public HeightWrappingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasure, int heightMeasure) {
        int mode = MeasureSpec.getMode(heightMeasure);
        if(mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
            super.onMeasure(widthMeasure, heightMeasure);
            int height = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasure, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if(h > height) height = h;
            }
            heightMeasure = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasure, heightMeasure);
    }
}
