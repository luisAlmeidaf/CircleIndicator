package com.luisfalmeida.circleindicator.circleindicator

import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.Interpolator
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import com.luisfalmeida.circleindicator.R
import com.luisfalmeida.circleindicator.circleindicator.model.Config
import com.luisfalmeida.circleindicator.circleindicator.model.IndicatorStyle
import kotlin.math.abs

open class BaseCircleIndicator : LinearLayout {

    private var mIndicatorMargin = -1
    private var mIndicatorWidth = -1
    private var mIndicatorUnselectedWidth = -1
    private var mIndicatorHeight = -1
    private var mIndicatorUnselectedHeight = -1
    private var mIndicatorBackgroundResId = 0
    private var mIndicatorUnselectedBackgroundResId = 0
    private var mIndicatorTintColor: ColorStateList? = null
    private var mIndicatorTintUnselectedColor: ColorStateList? = null
    private var mAnimatorOut: Animator? = null
    private var mAnimatorIn: Animator? = null
    private var mImmediateAnimatorOut: Animator? = null
    private var mImmediateAnimatorIn: Animator? = null
    var mLastPosition = -1

    @Nullable
    private var mIndicatorCreatedListener: IndicatorCreatedListener? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    fun setIndicatorCreatedListener(@Nullable indicatorCreatedListener: IndicatorCreatedListener?) {
        mIndicatorCreatedListener = indicatorCreatedListener
    }

    @JvmOverloads
    fun tintIndicator(
        @ColorInt indicatorColor: Int,
        @ColorInt unselectedIndicatorColor: Int = indicatorColor
    ) {
        mIndicatorTintColor = ColorStateList.valueOf(indicatorColor)
        mIndicatorTintUnselectedColor = ColorStateList.valueOf(unselectedIndicatorColor)
        changeIndicatorBackground()
    }

    @JvmOverloads
    fun changeIndicatorResource(
        @DrawableRes indicatorResId: Int,
        @DrawableRes indicatorUnselectedResId: Int = indicatorResId
    ) {
        mIndicatorBackgroundResId = indicatorResId
        mIndicatorUnselectedBackgroundResId = indicatorUnselectedResId
        changeIndicatorBackground()
    }

    fun createIndicators(count: Int, currentPosition: Int) {
        stopAnimatorRunning()
        setIndicatorPlaceholders(count)
        setIndicatorStyle(count, currentPosition)
        mLastPosition = currentPosition
    }

    fun animatePageSelected(position: Int) {
        if (mLastPosition == position) {
            return
        }
        stopAnimatorRunning()
        if (mAnimatorIn?.isRunning == true) {
            stopAnimator(mAnimatorIn)
        }
        if (mAnimatorOut?.isRunning == true) {
            stopAnimator(mAnimatorOut)
        }

        if (mLastPosition >= 0 && getChildAt(mLastPosition) != null) {
            val currentIndicator = getChildAt(mLastPosition)
            val indicatorStyle = IndicatorStyle(
                currentIndicator,
                mIndicatorUnselectedBackgroundResId,
                mIndicatorTintUnselectedColor,
                mIndicatorUnselectedWidth,
                mIndicatorUnselectedHeight
            )
            bindIndicatorBackground(indicatorStyle)
            currentIndicator.setTargetAndStartAnimation(mAnimatorIn)

        }
        val selectedIndicator: View? = getChildAt(position)
        if (selectedIndicator != null) {
            val indicatorStyle = IndicatorStyle(
                selectedIndicator,
                mIndicatorBackgroundResId,
                mIndicatorTintColor,
                mIndicatorWidth,
                mIndicatorHeight
            )
            bindIndicatorBackground(indicatorStyle)
            selectedIndicator.setTargetAndStartAnimation(mAnimatorOut)
        }
        mLastPosition = position
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val config: Config = handleTypedArray(context, attrs)
        initialize(config)
        if (isInEditMode) {
            createIndicators(QUANTITY_INDICATOR, BASE_POSITION)
        }
    }

    private fun handleTypedArray(context: Context, attrs: AttributeSet?): Config {
        val config = Config()
        if (attrs == null) {
            return config
        }
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.BaseCircleIndicator)
        config.width =
            typedArray.getDimensionPixelSize(
                R.styleable.BaseCircleIndicator_ci_deselected_width,
                -1
            )
        config.height =
            typedArray.getDimensionPixelSize(
                R.styleable.BaseCircleIndicator_ci_deselected_height,
                -1
            )
        config.margin =
            typedArray.getDimensionPixelSize(R.styleable.BaseCircleIndicator_ci_margin, -1)
        config.selectedWidth =
            typedArray.getDimensionPixelSize(R.styleable.BaseCircleIndicator_ci_width, -1)
        config.selectedHeight =
            typedArray.getDimensionPixelSize(R.styleable.BaseCircleIndicator_ci_height, -1)
        config.animatorResId = typedArray.getResourceId(
            R.styleable.BaseCircleIndicator_ci_animator,
            R.animator.scale_with_alpha
        )
        config.animatorReverseResId =
            typedArray.getResourceId(R.styleable.BaseCircleIndicator_ci_animator_reverse, 0)
        config.backgroundResId = typedArray.getResourceId(
            R.styleable.BaseCircleIndicator_ci_drawable,
            R.drawable.deselected_default
        )
        config.unselectedBackgroundId = typedArray.getResourceId(
            R.styleable.BaseCircleIndicator_ci_drawable_unselected,
            config.backgroundResId
        )
        config.orientation = typedArray.getInt(R.styleable.BaseCircleIndicator_ci_orientation, -1)
        config.gravity = typedArray.getInt(R.styleable.BaseCircleIndicator_ci_gravity, -1)
        typedArray.recycle()
        return config
    }

    private fun initialize(config: Config) {
        val miniSize = (TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_INDICATOR_WIDTH.toFloat(), resources.displayMetrics
        ) + SIZE_MULTIPLICATOR).toInt()
        getIndicatorSizeAndMarginConfig(config, miniSize)
        getIndicatorAnimators(config)
        getIndicatorBackground(config)
        getIndicatorOrientation(config)
        getIndicatorGravity(config)
    }

    private fun getIndicatorSizeAndMarginConfig(config: Config, miniSize: Int) {
        mIndicatorUnselectedWidth = if (config.width < 0) miniSize else config.width
        mIndicatorUnselectedHeight = if (config.height < 0) miniSize else config.height
        mIndicatorWidth = if (config.selectedWidth < 0) miniSize else config.selectedWidth
        mIndicatorHeight = if (config.selectedHeight < 0) miniSize else config.selectedHeight
        mIndicatorMargin = if (config.margin < 0) miniSize else config.margin
    }

    private fun getIndicatorAnimators(config: Config) {
        mAnimatorOut = createAnimatorOut(config)
        mImmediateAnimatorOut = createAnimatorOut(config)
        mImmediateAnimatorOut?.duration = 0
        mAnimatorIn = createAnimatorIn(config)
        mImmediateAnimatorIn = createAnimatorIn(config)
        mImmediateAnimatorIn?.duration = 0
    }

    private fun getIndicatorBackground(config: Config) {
        mIndicatorBackgroundResId =
            if (config.backgroundResId == 0) R.drawable.deselected_default else config.backgroundResId
        mIndicatorUnselectedBackgroundResId =
            if (config.unselectedBackgroundId == 0) config.backgroundResId else config.unselectedBackgroundId
    }

    private fun getIndicatorOrientation(config: Config) {
        orientation = if (config.orientation == VERTICAL) VERTICAL else HORIZONTAL
    }

    private fun getIndicatorGravity(config: Config) {
        gravity = if (config.gravity >= 0) config.gravity else Gravity.CENTER
    }

    private fun createAnimatorOut(config: Config): Animator {
        return AnimatorInflater.loadAnimator(context, config.animatorResId)
    }

    private fun createAnimatorIn(config: Config): Animator = if (config.animatorReverseResId == 0) {
        setReverseAnimation(config)
    } else {
        AnimatorInflater.loadAnimator(context, config.animatorReverseResId)
    }

    private fun setReverseAnimation(config: Config): Animator {
        val animator = AnimatorInflater.loadAnimator(context, config.animatorResId)
        animator.interpolator = ReverseInterpolator()
        return animator
    }

    private fun stopAnimatorRunning() {
        if (mImmediateAnimatorOut?.isRunning == true) {
            stopAnimator(mImmediateAnimatorOut)
        }
        if (mImmediateAnimatorIn?.isRunning == true) {
            stopAnimator(mImmediateAnimatorIn)
        }
    }

    private fun stopAnimator(animator: Animator?) {
        animator?.end()
        animator?.cancel()
    }

    private fun setIndicatorPlaceholders(count: Int) {
        val childViewCount = childCount
        if (count < childViewCount) {
            removeViews(count, childViewCount - count)
        } else if (count > childViewCount) {
            updateIndicatorCountAndAddIndicator(count, childViewCount)
        }
    }

    private fun updateIndicatorCountAndAddIndicator(count: Int, childViewCount: Int) {
        val addCount = count - childViewCount
        for (i in 0 until addCount) {
            addIndicator(orientation)
        }
    }

    private fun setIndicatorStyle(count: Int, currentPosition: Int) {
        var indicator: View
        var actualPosition = currentPosition
        var indicatorStyle: IndicatorStyle
        if (currentPosition == -1) actualPosition = 0
        for (i in 0 until count) {
            indicator = getChildAt(i)
            indicatorStyle = if (actualPosition == i) {
                createIndicatorAndSetSelectedStyleAndAnimation(indicator)
            } else {
                createIndicatorAndSetUnselectedStyleAndAnimation(indicator)
            }
            bindIndicatorBackground(indicatorStyle)
            removeIndicatorCreatedListenerIfNecessary(indicatorStyle.indicator, i)
        }
    }

    private fun createIndicatorAndSetUnselectedStyleAndAnimation(indicator: View): IndicatorStyle {
        val indicatorStyle = IndicatorStyle(
            indicator,
            mIndicatorUnselectedBackgroundResId,
            mIndicatorTintUnselectedColor,
            mIndicatorUnselectedWidth,
            mIndicatorUnselectedHeight
        )
        indicator.setTargetAndStartAnimation(mImmediateAnimatorIn)
        return indicatorStyle
    }

    private fun createIndicatorAndSetSelectedStyleAndAnimation(indicator: View): IndicatorStyle {
        val indicatorStyle = IndicatorStyle(
            indicator,
            mIndicatorBackgroundResId,
            mIndicatorTintColor,
            mIndicatorWidth,
            mIndicatorHeight
        )
        indicator.setTargetAndStartAnimation(mImmediateAnimatorOut)
        return indicatorStyle
    }

    private fun View.setTargetAndStartAnimation(animator: Animator?) {
        animator?.setTarget(this)
        animator?.start()
    }

    private fun removeIndicatorCreatedListenerIfNecessary(indicator: View?, i: Int) {
        if (mIndicatorCreatedListener != null) {
            mIndicatorCreatedListener!!.onIndicatorCreated(indicator, i)
        }
    }

    private fun addIndicator(orientation: Int) {
        val indicator = View(context)
        val params = generateDefaultLayoutParams()
        addWidthAndHeight(params)
        if (orientation == HORIZONTAL) {
            addLeftAndRightMargins(params)
        } else {
            addTopAndBottomMargins(params)
        }
        addView(indicator, params)
    }

    private fun addWidthAndHeight(params: LayoutParams) {
        params.width = mIndicatorWidth
        params.height = mIndicatorHeight
    }

    private fun addLeftAndRightMargins(params: LayoutParams) {
        params.leftMargin = mIndicatorMargin
        params.rightMargin = mIndicatorMargin
    }

    private fun addTopAndBottomMargins(params: LayoutParams) {
        params.topMargin = mIndicatorMargin
        params.bottomMargin = mIndicatorMargin
    }

    private fun changeIndicatorBackground() {
        val count = childCount
        if (count <= 0) {
            return
        }
        var currentIndicator: View
        for (i in 0 until count) {
            currentIndicator = getChildAt(i)
            if (i == mLastPosition) {
                val indicatorStyle = IndicatorStyle(
                    currentIndicator,
                    mIndicatorBackgroundResId,
                    mIndicatorTintColor,
                    mIndicatorWidth,
                    mIndicatorHeight
                )
                bindIndicatorBackground(indicatorStyle)
            } else {
                val indicatorStyle = IndicatorStyle(
                    currentIndicator,
                    mIndicatorUnselectedBackgroundResId,
                    mIndicatorTintUnselectedColor,
                    mIndicatorUnselectedWidth,
                    mIndicatorUnselectedHeight
                )
                bindIndicatorBackground(indicatorStyle)
            }
        }
    }

    private fun bindIndicatorBackground(
        indicatorStyle: IndicatorStyle
    ) {
        indicatorStyle.indicator?.let {
            val params = it.layoutParams
            params.height = indicatorStyle.height ?: DEFAULT_INDICATOR_HEIGHT
            params.width = indicatorStyle.width ?: DEFAULT_INDICATOR_WIDTH
            indicatorStyle.indicator.layoutParams = params
            if (indicatorStyle.indicatorColor != null) {
                ContextCompat.getDrawable(context, indicatorStyle.backgroundRes!!)?.mutate()?.let { drawable ->
                    val indicatorDrawable: Drawable = DrawableCompat.wrap(
                        drawable
                    )
                    DrawableCompat.setTintList(indicatorDrawable, indicatorStyle.indicatorColor)
                    ViewCompat.setBackground(indicatorStyle.indicator, indicatorDrawable)
                }
            } else {
                indicatorStyle.indicator.setBackgroundResource(indicatorStyle.backgroundRes!!)
            }
        }
    }

    private class ReverseInterpolator : Interpolator {
        override fun getInterpolation(value: Float): Float {
            return abs(1.0f - value)
        }
    }

    interface IndicatorCreatedListener {
        fun onIndicatorCreated(view: View?, position: Int)
    }

    companion object {
        private const val DEFAULT_INDICATOR_WIDTH = 5
        private const val DEFAULT_INDICATOR_HEIGHT = 5
        private const val QUANTITY_INDICATOR = 3
        private const val BASE_POSITION = 3
        private const val SIZE_MULTIPLICATOR = 0.5f
    }
}