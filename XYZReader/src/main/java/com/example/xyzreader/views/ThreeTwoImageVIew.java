package com.example.xyzreader.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by António "Curtes Malteser" Bastião on 12/05/2018.
 */

public class ThreeTwoImageVIew extends android.support.v7.widget.AppCompatImageView {
    public ThreeTwoImageVIew(Context context) {
        super(context);
    }

    public ThreeTwoImageVIew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreeTwoImageVIew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int threeTwoHeight = MeasureSpec.getSize(widthMeasureSpec) * 2 / 3;
        int threeTwoHeightSpec = MeasureSpec.makeMeasureSpec(threeTwoHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, threeTwoHeightSpec);
    }
}
