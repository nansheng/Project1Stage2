package com.learn.lonejourneyman.project1s2v00;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by lonejourneyman on 4/20/16.
 */
public class CustomizeListView extends ListView {

    public CustomizeListView(Context context) {
        super(context);
    }

    public CustomizeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomizeListView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
    }
}
