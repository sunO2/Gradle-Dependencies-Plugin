package com.hezhihu.testdemo.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CustomTextView : AppCompatTextView {

    constructor(context: Context): super(context)
    constructor(context: Context,attrs: AttributeSet): super(context,attrs)
    constructor(context: Context,attrs: AttributeSet,defStyleAttr: Int): super(context,attrs,defStyleAttr)

    override fun setTypeface(tf: Typeface?) {
        super.setTypeface(tf)
    }

    override fun setTypeface(tf: Typeface?, style: Int) {
        super.setTypeface(tf, style)
    }

}