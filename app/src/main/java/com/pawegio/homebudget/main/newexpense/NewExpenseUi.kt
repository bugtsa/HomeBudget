package com.pawegio.homebudget.main.newexpense

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.pawegio.homebudget.R
import com.pawegio.homebudget.util.colorAttr
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import splitties.dimensions.dip
import splitties.views.*
import splitties.views.dsl.appcompat.toolbar
import splitties.views.dsl.constraintlayout.*
import splitties.views.dsl.coordinatorlayout.appBarLParams
import splitties.views.dsl.coordinatorlayout.coordinatorLayout
import splitties.views.dsl.core.*
import splitties.views.dsl.material.appBarLayout
import splitties.views.dsl.material.contentScrollingWithAppBarLParams
import splitties.views.dsl.material.defaultLParams

class NewExpenseUi(override val ctx: Context) : Ui {

    var note: String?
        get() = noteEditText.editableText.toString()
        set(value) {
            noteEditText.setText(value)
        }

    var date: LocalDate? = null
        set(value) {
            field = value
            dateTextView.text = value?.format(DateTimeFormatter.ofPattern("eeee, d MMMM yyyy"))
        }

    var onBackClick: (() -> Unit)? = null

    private val appBar = appBarLayout(theme = R.style.AppTheme_AppBarOverlay) {
        add(toolbar {
            (ctx as? AppCompatActivity)?.setSupportActionBar(this)
            popupTheme = R.style.AppTheme_PopupOverlay
            setNavigationIcon(R.drawable.ic_close)
            setTitle(R.string.new_expense)
            setNavigationOnClickListener { onBackClick?.invoke() }
        }, defaultLParams(height = matchParent))
    }

    private val noteImageView = imageView {
        imageResource = R.drawable.ic_note
        setColorFilter(colorAttr(R.attr.colorPrimary))
        padding = dip(8)
    }

    private val noteEditText = editText {
        textAppearance = R.style.TextAppearance_MaterialComponents_Headline5
        isSingleLine = true
        setHint(R.string.note_hint)
        verticalPadding = dip(8)
    }

    private val dateImageView = imageView {
        imageResource = R.drawable.ic_date
        setColorFilter(colorAttr(R.attr.colorPrimary))
        padding = dip(8)
    }

    private val dateTextView = textView {
        textAppearance = R.style.TextAppearance_MaterialComponents_Body1
    }

    private val categoryImageView = imageView {
        imageResource = R.drawable.ic_category
        setColorFilter(colorAttr(R.attr.colorPrimary))
        padding = dip(8)
    }

    val categorySpinner = spinner {
        verticalPadding = dip(8)
    }

    private val amountImageView = imageView {
        imageResource = R.drawable.ic_amount
        setColorFilter(colorAttr(R.attr.colorPrimary))
        padding = dip(8)
    }

    private val amountEditText = editText {
        isSingleLine = true
        gravity = gravityEnd
        setHint(R.string.amount_hint)
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    }

    val addExpenseButton = button {
        textResource = R.string.add
    }

    override val root: View = coordinatorLayout {
        add(appBar, appBarLParams())
        add(constraintLayout {
            add(noteImageView, lParams(dip(40), dip(40)) {
                topOfParent(dip(8))
                startOfParent(dip(16))
                verticalMargin = dip(8)
            })
            add(noteEditText, lParams(matchConstraints, wrapContent) {
                alignVerticallyOn(noteImageView)
                startToEndOf(noteImageView, dip(16))
                endOfParent(dip(16))
            })
            add(dateImageView, lParams(dip(40), dip(40)) {
                topToBottomOf(noteImageView, dip(16))
                startOfParent(dip(16))
                verticalMargin = dip(8)
            })
            add(dateTextView, lParams(matchConstraints, wrapContent) {
                alignVerticallyOn(dateImageView)
                startToEndOf(dateImageView, dip(16))
                endOfParent(dip(16))
            })
            add(categoryImageView, lParams(dip(40), dip(40)) {
                topToBottomOf(dateImageView, dip(16))
                startOfParent(dip(16))
                verticalMargin = dip(8)
            })
            add(categorySpinner, lParams(matchConstraints, wrapContent) {
                alignVerticallyOn(categoryImageView)
                startToEndOf(categoryImageView, dip(16))
                endOfParent(dip(16))
            })
            add(amountImageView, lParams(dip(40), dip(40)) {
                topToBottomOf(categoryImageView, dip(16))
                startOfParent(dip(16))
                verticalMargin = dip(8)
            })
            add(amountEditText, lParams(matchConstraints, wrapContent) {
                alignVerticallyOn(amountImageView)
                startToEndOf(amountImageView, dip(16))
                endOfParent(dip(16))
            })
            add(addExpenseButton, lParams(wrapContent, wrapContent) {
                topToBottomOf(amountImageView, dip(16))
                startOfParent(dip(16))
            })
        }, contentScrollingWithAppBarLParams())
    }
}

@Suppress("unused")
private class NewExpenseUiPreview : FrameLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        addView(NewExpenseUi(context).apply {
            date = LocalDate.parse("2020-07-21")
        }.root)
    }
}
