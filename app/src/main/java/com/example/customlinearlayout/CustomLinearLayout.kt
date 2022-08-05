package com.example.customlinearlayout


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.ColorInt


class CustomLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var orientation = HORIZONTAL
    private var weightSum = 0f
    private var cornerRadius = 0f

    @ColorInt
    private var borderColor = Color.parseColor(COLOR_WHITE)
    private var borderWidth = 0f
    private val strokeWidth = 10 * resources.displayMetrics.density

    init {
        initAttrs(attrs, defStyleAttr, defStyleRes)
        setWillNotDraw(false)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomLinearLayout_Layout, defStyleAttr, defStyleRes)
        orientation = typedArray.getInt(R.styleable.CustomLinearLayout_Layout_android_orientation, 0)
        weightSum = typedArray.getFloat(R.styleable.CustomLinearLayout_Layout_android_weightSum, 0f)
        cornerRadius = typedArray.getDimension(R.styleable.CustomLinearLayout_Layout_cornerRadius, 0f)
        borderWidth = typedArray.getDimension(R.styleable.CustomLinearLayout_Layout_borderWidth, 0f)
        borderColor = typedArray.getColor(R.styleable.CustomLinearLayout_Layout_borderColor, Color.parseColor(COLOR_WHITE))
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0..childCount) {
            val child = getChildAt(i)
            child?.let {
                measureChild(it, widthMeasureSpec, heightMeasureSpec)
            }
        }

    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        for (i in 0..childCount) {
            val child = getChildAt(i)
            child?.let {
                val circle = Paint(Paint.ANTI_ALIAS_FLAG)
                circle.style = Paint.Style.STROKE
                circle.strokeWidth = strokeWidth
                circle.strokeCap = Paint.Cap.SQUARE
                circle.color = (background as? ColorDrawable)?.color ?: context.getColor(android.R.color.background_light)
                val mArcBounds = RectF()
                mArcBounds.set(
                    it.x - circle.strokeWidth / 2,
                    it.y - circle.strokeWidth / 2,
                    circle.strokeWidth / 2 + it.x + it.width,
                    circle.strokeWidth / 2 + it.y + it.height
                )
                if (cornerRadius > 0) {
                    canvas?.drawRoundRect(mArcBounds, cornerRadius, cornerRadius, circle)
                }
                if (borderWidth > 0) {
                    val border = Paint(Paint.ANTI_ALIAS_FLAG)
                    border.style = Paint.Style.STROKE
                    border.strokeWidth = borderWidth
                    border.strokeCap = Paint.Cap.SQUARE
                    border.color = borderColor
                    mArcBounds.set(
                        it.x - border.strokeWidth / 2,
                        it.y - border.strokeWidth / 2,
                        border.strokeWidth / 2 + it.x + it.width,
                        border.strokeWidth / 2 + it.y + it.height
                    )
                    canvas?.drawRoundRect(mArcBounds, cornerRadius, cornerRadius, border)
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (orientation == VERTICAL)
            layoutVertical()
        else
            layoutHorizontal()
    }

    private fun layoutVertical() {
        var realHeight = 0
        if (weightSum == 0f)
            for (i in 0..childCount) {
                getChildAt(i)?.let {
                    val weight = (it.layoutParams as CustomLayoutParams).weight
                    if (weight == 0f) weightSum += 1 else weightSum += weight
                }
            }
        val border = if (cornerRadius > 0 && strokeWidth > borderWidth) strokeWidth.toInt() else borderWidth.toInt()
        for (i in 0..childCount) {
            getChildAt(i)?.let {
                val lp = it.layoutParams as CustomLayoutParams
                val partOfLayout = when (lp.height) {
                    MATCH_PARENT -> lp.weight / weightSum + (it.paddingTop + it.paddingBottom) / height.toFloat()
                    WRAP_CONTENT -> (it.paddingTop + it.paddingBottom + it.measuredHeight) / height.toFloat()
                    else -> (it.paddingTop + it.paddingBottom + lp.width) / width.toFloat() * lp.weight
                }
                realHeight += lp.topMargin + lp.bottomMargin + border * 2
                realHeight += if (lp.weight == 0f)
                    when (lp.height) {
                        WRAP_CONTENT -> it.measuredHeight
                        MATCH_PARENT -> height
                        else -> lp.height
                    }
                else
                    (partOfLayout * height).toInt()
            }
        }
        val scaleMultiplier = if (realHeight <= height) 1f else height / realHeight.toFloat()
        var layoutHeight = 0
        for (i in 0..childCount) {
            val child = getChildAt(i)
            child?.let {
                val lp = it.layoutParams as CustomLayoutParams
                val partOfLayout = when (lp.height) {
                    MATCH_PARENT -> lp.weight / weightSum + (it.paddingTop + it.paddingBottom) / height.toFloat()
                    WRAP_CONTENT -> (it.paddingTop + it.paddingBottom + it.measuredHeight) / height.toFloat()
                    else -> (it.paddingTop + it.paddingBottom + lp.width) / width.toFloat() * lp.weight
                }
                var childHeight =
                    if (lp.weight == 0f)
                        when (lp.height) {
                            WRAP_CONTENT -> it.measuredHeight
                            MATCH_PARENT -> height
                            else -> lp.height
                        }
                    else
                        (partOfLayout * height).toInt()
                val childWidth = when (lp.width) {
                    WRAP_CONTENT -> it.measuredWidth
                    MATCH_PARENT -> width
                    else -> lp.width
                }
                childHeight = (childHeight * scaleMultiplier).toInt()
                val marginLeft = lp.leftMargin
                val marginRight = lp.rightMargin
                val marginTop = ((lp.topMargin + border) * scaleMultiplier).toInt()
                val marginBottom = ((lp.bottomMargin + border) * scaleMultiplier).toInt()
                layoutHeight += marginTop
                it.layout(
                    marginLeft,
                    layoutHeight,
                    childWidth + marginRight,
                    layoutHeight + childHeight - marginTop
                )
                layoutHeight += childHeight - marginTop + marginBottom
            }
        }
    }

    private fun layoutHorizontal() {
        var realWidth = 0
        if (weightSum == 0f)
            for (i in 0..childCount) {
                getChildAt(i)?.let {
                    val weight = (it.layoutParams as CustomLayoutParams).weight
                    if (weight == 0f) weightSum += 1 else weightSum += weight
                }
            }
        val border = if (cornerRadius > 0 && strokeWidth > borderWidth) strokeWidth.toInt() else borderWidth.toInt()
        for (i in 0..childCount) {
            getChildAt(i)?.let {
                val lp = it.layoutParams as CustomLayoutParams
                val partOfLayout = when (lp.width) {
                    MATCH_PARENT -> lp.weight / weightSum + (it.paddingRight + it.paddingLeft) / width.toFloat()
                    WRAP_CONTENT -> (it.paddingRight + it.paddingLeft + it.measuredWidth) / width.toFloat()
                    else -> (it.paddingRight + it.paddingLeft + lp.width) / width.toFloat() * lp.weight
                }
                realWidth += lp.marginStart + lp.marginEnd + border * 2
                realWidth += if (lp.weight == 0f)
                    when (lp.width) {
                        WRAP_CONTENT -> it.measuredWidth
                        MATCH_PARENT -> width
                        else -> lp.width
                    }
                else
                    (partOfLayout * width).toInt()
            }
        }
        val scaleMultiplier = if (realWidth <= width) 1f else width / realWidth.toFloat()
        var layoutWidth = 0
        for (i in 0..childCount) {
            val child = getChildAt(i)
            child?.let {
                val lp = it.layoutParams as CustomLayoutParams
                val partOfLayout = when (lp.width) {
                    MATCH_PARENT -> lp.weight / weightSum + (it.paddingRight + it.paddingLeft) / width.toFloat()
                    WRAP_CONTENT -> (it.paddingRight + it.paddingLeft + it.measuredWidth) / width.toFloat()
                    else -> (it.paddingRight + it.paddingLeft + lp.width) / width.toFloat() * lp.weight
                }
                var childWidth =
                    if (lp.weight == 0f)
                        when (lp.width) {
                            WRAP_CONTENT -> it.measuredWidth
                            MATCH_PARENT -> width
                            else -> lp.width
                        }
                    else
                        (partOfLayout * width).toInt()
                val childHeight = when (lp.height) {
                    WRAP_CONTENT -> it.measuredHeight
                    MATCH_PARENT -> height
                    else -> lp.height
                }
                childWidth = (childWidth * scaleMultiplier).toInt()
                val marginLeft = ((lp.leftMargin + border) * scaleMultiplier).toInt()
                val marginRight = ((lp.rightMargin + border) * scaleMultiplier).toInt()
                val marginTop = lp.topMargin + border
                val marginBottom = lp.bottomMargin
                layoutWidth += marginLeft
                it.layout(
                    layoutWidth,
                    marginTop,
                    layoutWidth + childWidth - marginLeft,
                    childHeight + marginBottom
                )
                layoutWidth += childWidth + marginRight
            }
        }
    }

    override fun measureChild(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)

        val childWidthSpec = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> widthMeasureSpec
            MeasureSpec.AT_MOST -> widthMeasureSpec
            MeasureSpec.EXACTLY -> MeasureSpec.makeMeasureSpec(widthSpecSize, MeasureSpec.AT_MOST)
            else -> error("Unreachable")
        }


        val childHeightSpec = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> heightMeasureSpec
            MeasureSpec.AT_MOST -> heightMeasureSpec
            MeasureSpec.EXACTLY -> MeasureSpec.makeMeasureSpec(heightSpecSize, MeasureSpec.AT_MOST)
            else -> error("Unreachable")
        }

        child.measure(childWidthSpec, childHeightSpec)
    }

    override fun generateDefaultLayoutParams(): CustomLayoutParams? {
        if (orientation == HORIZONTAL) {
            return CustomLayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
            )
        } else if (orientation == VERTICAL) {
            return CustomLayoutParams(
                MATCH_PARENT,
                WRAP_CONTENT
            )
        }
        return null
    }

    override fun generateLayoutParams(attrs: AttributeSet?): CustomLayoutParams {
        return CustomLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): CustomLayoutParams? {
        return generateDefaultLayoutParams()
    }

    inner class CustomLayoutParams : MarginLayoutParams {
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            val typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.CustomLinearLayout_Layout
            )

            weight = typedArray.getFloat(R.styleable.CustomLinearLayout_Layout_android_layout_weight, 0f)
            typedArray.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: LayoutParams) : super(source)

        var weight = 0f
    }

    companion object {
        private const val HORIZONTAL = 0
        private const val VERTICAL = 1
        private const val COLOR_WHITE = "#FFFFFFFF"
    }
}
