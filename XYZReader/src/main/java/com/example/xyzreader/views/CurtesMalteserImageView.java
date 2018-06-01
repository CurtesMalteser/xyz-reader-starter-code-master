package com.example.xyzreader.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.example.xyzreader.R;

/**
 * Created by António "Curtes Malteser" Bastião on 12/05/2018.
 */

public class CurtesMalteserImageVIew extends android.support.v7.widget.AppCompatImageView {

    private String aspectRatio = "";

    public CurtesMalteserImageVIew(Context context) {
        super(context);
    }

    public CurtesMalteserImageVIew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CurtesMalteserImageVIew,
                0, 0);
        try {
            aspectRatio = a.getString(R.styleable.CurtesMalteserImageVIew_aspect_ratio);
        } finally {
            a.recycle();
        }
    }

    public CurtesMalteserImageVIew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (aspectRatio.equals("3:2") || aspectRatio.equals("")) {
            int threeTwoHeight = MeasureSpec.getSize(widthMeasureSpec) * 2 / 3;
            int threeTwoHeightSpec = MeasureSpec.makeMeasureSpec(threeTwoHeight, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, threeTwoHeightSpec);
        } else if (aspectRatio.equals("16:9")) {
            int threeTwoHeight = MeasureSpec.getSize(widthMeasureSpec) * 9 / 16;
            int threeTwoHeightSpec = MeasureSpec.makeMeasureSpec(threeTwoHeight, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, threeTwoHeightSpec);
        } else if (aspectRatio.equals("20:9")) {
            int threeTwoHeight = MeasureSpec.getSize(widthMeasureSpec) * 9 / 20;
            int threeTwoHeightSpec = MeasureSpec.makeMeasureSpec(threeTwoHeight, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, threeTwoHeightSpec);
        }
    }
}
