package com.binzeefox.mdpmrd.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by tong.xiwen on 2017/6/8.
 */
public class AnimationUtil {

    public static ObjectAnimator fadeOut(View v){
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 1f,0f);
        return animator;
    }

    public static ObjectAnimator fadeIn(View v){
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 0f,1f);
        return animator;
    }
}
