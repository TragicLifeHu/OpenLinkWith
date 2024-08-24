package com.tasomaniac.openwith.intro

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.tasomaniac.openwith.intro.lib.R

class CircularIndicatorView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var mDots: MutableList<ImageView>? = null
    private var mSlideCount = 0
    private var selectedDotColor = DEFAULT_COLOR
    private var unselectedDotColor = DEFAULT_COLOR

    private var mCurrentPosition = 0

    init {
        gravity = Gravity.CENTER
        orientation = HORIZONTAL
    }

    fun initialize(slideCount: Int) {
        mDots = ArrayList()
        mSlideCount = slideCount
        selectedDotColor = -1
        unselectedDotColor = -1

        for (i in 0 until slideCount) {
            val dot = ImageView(context)
            dot.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.indicator_dot_grey))

            val params = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )

            addView(dot, params)
            (mDots as ArrayList<ImageView>).add(dot)
        }

        selectPosition(FIRST_PAGE_NUM)
    }

    fun selectPosition(index: Int) {
        mCurrentPosition = index
        for (i in 0 until mSlideCount) {
            val drawableId = if (i == index) R.drawable.indicator_dot_white else R.drawable.indicator_dot_grey
            val drawable = ContextCompat.getDrawable(context, drawableId)
            if (i == index) {
                val colorFilter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    BlendModeColorFilter(selectedDotColor, BlendMode.SRC_IN)} else {
                    PorterDuffColorFilter(selectedDotColor, PorterDuff.Mode.SRC_IN)
                }
                drawable!!.mutate().colorFilter = colorFilter
            } else {
                val colorFilter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    BlendModeColorFilter(unselectedDotColor, BlendMode.SRC_IN)
                } else {
                    PorterDuffColorFilter(unselectedDotColor, PorterDuff.Mode.SRC_IN)
                }
                drawable!!.mutate().colorFilter = colorFilter
            }
            mDots!![i].setImageDrawable(drawable)
        }
    }

    companion object {
        private const val FIRST_PAGE_NUM = 0
        private const val DEFAULT_COLOR = 1
    }
}
