package com.luisfalmeida.circleindicator.circleindicator

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.SnapHelper

class CircleIndicator : BaseCircleIndicator {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSnapHelper: SnapHelper

    constructor(context: Context?) : super(context!!) {}

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @JvmOverloads constructor(
        context: Context?, attrs: AttributeSet?, defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context!!, attrs, defStyleAttr, defStyleRes) {
    }

    fun attachToRecyclerView(
        @NonNull recyclerView: RecyclerView,
        @NonNull snapHelper: SnapHelper
    ) {
        mRecyclerView = recyclerView
        mSnapHelper = snapHelper
        mLastPosition = 0
        createIndicators()
        recyclerView.removeOnScrollListener(mInternalOnScrollListener)
        recyclerView.addOnScrollListener(mInternalOnScrollListener)
    }

    private fun createIndicators() {
        val adapter = mRecyclerView.adapter
        val count: Int = adapter?.itemCount ?: 0
        createIndicators(count, getSnapPosition(mRecyclerView.layoutManager))
    }

    fun getSnapPosition(@Nullable layoutManager: RecyclerView.LayoutManager?): Int {
        if (layoutManager == null) {
            return RecyclerView.NO_POSITION
        }
        val snapView = mSnapHelper.findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
    }

    private val mInternalOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position = getSnapPosition(recyclerView.layoutManager)
                if (position == RecyclerView.NO_POSITION) {
                    return
                }
                animatePageSelected(position)
            }
        }
    val adapterDataObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            val adapter = mRecyclerView.adapter
            val newCount = adapter?.itemCount ?: 0
            val currentCount = childCount
            if (newCount == currentCount) {
                // No change
                return
            } else if (mLastPosition < newCount) {
                mLastPosition = getSnapPosition(mRecyclerView.layoutManager)
            } else {
                mLastPosition = RecyclerView.NO_POSITION
            }
            createIndicators()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            onChanged()
        }

        override fun onItemRangeChanged(
            positionStart: Int, itemCount: Int,
            @Nullable payload: Any?
        ) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            onChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            onChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            onChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            onChanged()
        }
    }
}