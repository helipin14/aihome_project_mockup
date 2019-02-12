package com.itcs.aihome;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        if(parent.getChildLayoutPosition(view) == 0 && parent.getChildLayoutPosition(view) == 1) {
            outRect.top = 0;
        } else {
            outRect.top = space;
        }
    }
}
