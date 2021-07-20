package com.luisfalmeida.circleindicator.circleindicator.model

import android.content.res.ColorStateList
import android.view.View

data class IndicatorStyle(val indicator: View? = null,
                          val backgroundRes: Int? = null,
                          val indicatorColor: ColorStateList? = null,
                          val width: Int? = null,
                          val height: Int? = null
                          )
