package com.app.View;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.third.app.R;

public class Look_MarginDecoration extends RecyclerView.ItemDecoration {
    private int margin;

    public Look_MarginDecoration(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.look_item_margin);
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(margin, margin, margin, margin);
    }
}