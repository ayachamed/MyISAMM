package com.example.myisamm;

import android.view.animation.DecelerateInterpolator;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.lang.reflect.Field;

public class ViewPager2Utils {
    public static void reduceDragSensitivity(ViewPager2 viewPager2) {
        try {
            RecyclerView recyclerView = (RecyclerView) viewPager2.getChildAt(0);
            Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);
            int touchSlop = (int) touchSlopField.get(recyclerView);
            touchSlopField.set(recyclerView, touchSlop * 4); // Increase to make it less sensitive
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSmoothScrollDuration(ViewPager2 viewPager2, long durationMillis) {
        try {
            RecyclerView recyclerView = (RecyclerView) viewPager2.getChildAt(0);
            Field scrollerField = recyclerView.getClass().getDeclaredField("mViewFlinger");
            scrollerField.setAccessible(true);
            Object flinger = scrollerField.get(recyclerView);

            Field interpolatorField = flinger.getClass().getDeclaredField("mInterpolator");
            interpolatorField.setAccessible(true);
            interpolatorField.set(flinger, new DecelerateInterpolator());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
