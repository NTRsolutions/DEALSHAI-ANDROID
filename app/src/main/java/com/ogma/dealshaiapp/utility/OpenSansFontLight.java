package com.ogma.dealshaiapp.utility;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by AndroidDev on 10-10-2017.
 */

public class OpenSansFontLight extends AppCompatTextView {

    public OpenSansFontLight(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public OpenSansFontLight(Context context) {
        super(context);
        init();
    }

    public OpenSansFontLight(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "gothic.ttf");
        setTypeface(tf);
    }
}
