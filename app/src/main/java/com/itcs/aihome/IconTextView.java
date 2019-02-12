package com.itcs.aihome;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class IconTextView extends AppCompatTextView {
    public IconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "FontAwesome5Brands_Regular_400.otf");
        setTypeface(tf);
    }
}
