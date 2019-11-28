package com.github.watr.helpers

import android.animation.ObjectAnimator
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.willowtreeapps.spruce.Spruce
import com.willowtreeapps.spruce.sort.DefaultSort

class spruceHelper{

    fun animateSlideInLeft(animView: ViewGroup){
        val textViewAnimator = ObjectAnimator.ofFloat(animView, "translationX", -1000f, 0f)
        textViewAnimator.duration = 1000
        textViewAnimator.interpolator = AccelerateDecelerateInterpolator()

        Spruce.SpruceBuilder(animView)
            .sortWith(DefaultSort(30))
            .animateWith(
                textViewAnimator
            )
            .start()
    }

    fun animateSlideInRight(animView: ViewGroup){
        val textViewAnimator = ObjectAnimator.ofFloat(animView, "translationX", 1000f,0f)
        textViewAnimator.duration = 1000
        textViewAnimator.interpolator = AccelerateDecelerateInterpolator()

        Spruce.SpruceBuilder(animView)
            .sortWith(DefaultSort(50))
            .animateWith(
                textViewAnimator
            )
            .start()
    }
}