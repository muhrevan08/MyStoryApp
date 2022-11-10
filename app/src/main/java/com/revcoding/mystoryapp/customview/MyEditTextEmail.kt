package com.revcoding.mystoryapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.revcoding.mystoryapp.R

class MyEditTextEmail: AppCompatEditText {

    private lateinit var emailIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        hint = "Masukkan Email"

    }

    private fun init() {
        emailIcon = ContextCompat.getDrawable(context, R.drawable.ic_email) as Drawable
        setIconDrawable(startOfTheText = emailIcon)

        addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                isEmailValid(s.toString())

            }
        })
    }

    private fun setIconDrawable(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    private fun isEmailValid(email: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error = "Not a valid Email"
        }
        else return

    }


}