package com.example.triptracks.Presenter;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StackLayoutManager extends LinearLayoutManager {

    private static final float SCALE_FACTOR = 0.9f;
    private static final float ELEVATION_FACTOR = 5f;
    private static final int ITEM_OFFSET = 300; //solapamiento

    public StackLayoutManager(Context context) {
        super(context);
    }

    public StackLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public StackLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        super.onLayoutChildren(recycler, state);
        applyScaleAndElevation();
    }

    @Override
    public void onItemsRemoved(@NonNull RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsRemoved(recyclerView, positionStart, itemCount);
        applyScaleAndElevation();
    }


    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        applyScaleAndElevation();
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    public void scrollToPositionWithOffset(int position, int offset) {
        super.scrollToPositionWithOffset(position, offset);
    }

    public void applyScaleAndElevation() {
        int childCount = getChildCount();
        int width = getWidth();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child != null) {
                float scale = 1 - (childCount - 1 - i) * (1 - SCALE_FACTOR);
                child.setScaleX(scale);
                child.setScaleY(scale);
                child.setElevation(i * ELEVATION_FACTOR);
                child.setTranslationY((childCount - 1 - i) * ITEM_OFFSET);
                Rect clipBounds = new Rect(0, -child.getTop(), width, child.getHeight());
                child.setClipBounds(clipBounds);
            }
        }
    }
}
