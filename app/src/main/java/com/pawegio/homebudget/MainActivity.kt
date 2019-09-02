package com.pawegio.homebudget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.main_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        lifecycle.addObserver(CurrentActivityObserver)
        viewModel.appState.observe(this, Observer(::updateView))
        viewModel.monthlyBudget.observe(this, Observer(::updateMonthlyBudget))
        signInButton.setOnClickListener { viewModel.mainActions.offer(MainAction.SelectSignIn) }
    }

    private fun updateView(state: AppState?) {
        signInButton.isVisible = state is AppState.Unauthorized
    }

    private fun updateMonthlyBudget(monthlyBudget: MonthlyBudget?) {
        monthlyBudgetTextView.text =
            monthlyBudget?.let { (plannedIncomes, plannedExpenses, actualIncomes, actualExpenses, categories) ->
                val incomes = categories.first()
                listOf(
                    "Planowane przychody: $plannedIncomes",
                    "Planowane wydatki: $plannedExpenses",
                    "Rzeczywiste przychody: $actualIncomes",
                    "Rzeczywiste wydatki: $actualExpenses\n",
                    incomes.let { "${it.name}:\n${it.actual} / ${it.planned}\n" },
                    incomes.subcategories
                        .joinToString("\n\n") { "${it.name}:\n${it.actual} / ${it.planned}" }
                ).joinToString("\n")
            }
    }
}
