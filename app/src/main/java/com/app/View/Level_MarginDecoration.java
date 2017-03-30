package com.app.View;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.third.app.R;

public class Level_MarginDecoration extends RecyclerView.ItemDecoration {
    private int margin;

    public Level_MarginDecoration(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.level_item_margin);
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(margin, margin, margin, margin);
    }
}