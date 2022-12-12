package com.pawegio.homebudget.main.currencies

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import com.pawegio.homebudget.R
import com.pawegio.homebudget.main.transaction.TransactionUi
import com.pawegio.homebudget.util.colorAttr
import io.reactivex.Observable
import org.threeten.bp.LocalDate
import splitties.dimensions.dip
import splitties.views.*
import splitties.views.dsl.appcompat.toolbar
import splitties.views.dsl.constraintlayout.*
import splitties.views.dsl.coordinatorlayout.appBarLParams
import splitties.views.dsl.coordinatorlayout.coordinatorLayout
import splitties.views.dsl.core.*
import splitties.views.dsl.material.MaterialComponentsStyles
import splitties.views.dsl.material.appBarLayout
import splitties.views.dsl.material.contentScrollingWithAppBarLParams
import splitties.views.dsl.material.defaultLParams

class CurrenciesUi(override val ctx: Context) : Ui {

    private val backClicksRelay = PublishRelay.create<Unit>()
    private val exchangeCurrencyRelay = PublishRelay.create<Unit>()

    val backClicks: Observable<Unit> = backClicksRelay
    val exchangeClick: Observable<Unit> = exchangeCurrencyRelay

    private val materialStyles = MaterialComponentsStyles(ctx)

    private val appBar = appBarLayout(theme = R.style.AppTheme_AppBarOverlay) {
        add(toolbar {
            (ctx as? AppCompatActivity)?.setSupportActionBar(this)
            popupTheme = R.style.AppTheme_PopupOverlay
            setNavigationIcon(R.drawable.ic_close)
            setTitle(R.string.new_transaction)
            setNavigationOnClickListener { backClicksRelay.accept(Unit) }
        }, defaultLParams(height = matchParent))
    }

    private val currencyTextView = textView {
        textAppearance = R.style.TextAppearance_MaterialComponents_Body1
        textSize = 26f
        textResource = R.string.currency
    }

    private val exchangeButton = imageView {
        imageResource = R.drawable.ic_exchange
        setColorFilter(colorAttr(R.attr.colorOnPrimary))
        padding = dip(8)
        clicks()
            .subscribe(exchangeCurrencyRelay)
    }


    private val formLayout = constraintLayout {
        backgroundColor = colorAttr(R.attr.colorSurface)

        add(currencyTextView, lParams(wrapContent, wrapContent) {
            startOfParent(dip(16))
            endToStartOf(exchangeButton)
        })
        add(exchangeButton, lParams(wrapContent, wrapContent) {
            startToEndOf(currencyTextView, dip(4))
            endOfParent(dip(16))
        })
    }

    override val root: View = coordinatorLayout {
        add(appBar, appBarLParams())
        add(formLayout, contentScrollingWithAppBarLParams())
    }
}

@Suppress("unused")
private class CurrenciesUiPreview : FrameLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        addView(TransactionUi(context).apply {
            date = LocalDate.parse("2020-07-21")
        }.root)
    }
}