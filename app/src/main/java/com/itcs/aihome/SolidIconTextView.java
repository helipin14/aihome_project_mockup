package com.itcs.aihome;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class SolidIconTextView extends AppCompatTextView {
    public SolidIconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SolidIconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SolidIconTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "FontAwesome5Pro_Solid_900.otf");
        setTypeface(tf);
    }
}
