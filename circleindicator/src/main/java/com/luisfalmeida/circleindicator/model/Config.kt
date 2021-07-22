package com.luisfalmeida.circleindicator.model

import android.view.Gravity
import android.widget.LinearLayout
import androidx.annotation.AnimatorRes
import androidx.annotation.DrawableRes
import com.luisfalmeida.circleindicator.R

data class Config(var width: Int = -1,
                  var height: Int = -1,
                  var selectedWidth: Int = -1,
                  var selectedHeight: Int = -1,
                  var margin: Int = -1,
                  @AnimatorRes var animatorResId: Int = R.animator.scale_with_alpha,
                  @AnimatorRes var animatorReverseResId: Int = 0,
                  @DrawableRes var backgroundResId: Int = R.drawable.deselected_default,
                  @DrawableRes var unselectedBackgroundId: Int = 0,
                  var orientation: Int = LinearLayout.HORIZONTAL,
                  var gravity: Int = Gravity.CENTER)