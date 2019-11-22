package com.pawegio.homebudget.main

import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.pawegio.homebudget.FlowSpec
import com.pawegio.homebudget.MonthlyBudget
import com.pawegio.homebudget.util.MockHomeBudgetApi
import com.pawegio.homebudget.util.SpreadsheetLauncher
import com.pawegio.homebudget.util.createMonthlyBudget
import io.kotlintest.shouldBe
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.Month
import org.threeten.bp.ZoneId
import kotlin.coroutines.resume

internal class MainFlowTest : FlowSpec({
    "On main flow" - {
        val actions = Channel<MainAction>()
        val monthlyBudget = MutableLiveData<MonthlyBudget>()
        val monthType = MutableLiveData<MonthType>()
        val isLoading = MutableLiveData<Boolean>()
        val api = MockHomeBudgetApi()
        val spreadsheetLauncher = mock<SpreadsheetLauncher>()
        var clock = Clock.fixed(Instant.parse("2019-04-01T10:15:00.00Z"), ZoneId.systemDefault())

        val flow = launch(start = CoroutineStart.LAZY) {
            @Suppress("EXPERIMENTAL_API_USAGE")
            MainFlow(
                actions.consumeAsFlow(),
                monthType,
                monthlyBudget,
                isLoading,
                api,
                spreadsheetLauncher,
                clock
            )
        }

        "on January" - {
            clock = Clock.fixed(Instant.parse("2019-01-01T10:15:00.00Z"), ZoneId.systemDefault())
            flow.start()

            "is first month" {
                monthType.test().assertValue(MonthType.FIRST)
            }
        }

        "on December" - {
            clock = Clock.fixed(Instant.parse("2019-12-01T10:15:00.00Z"), ZoneId.systemDefault())
            flow.start()

            "is last month" {
                monthType.test().assertValue(MonthType.LAST)
            }
        }

        flow.start()

        "is middle month" {
            monthType.test().assertValue(MonthType.MIDDLE)
        }

        "get monthly budget for current month" {
            api.getMonthlyBudget.invocations shouldBe listOf(Month.APRIL)
        }

        "show loader" {
            isLoading.test().assertValue(true)
        }

        "on monthly budget loaded with success" - {
            val loadedMonthlyBudget = createMonthlyBudget()
            api.getMonthlyBudget.resume(loadedMonthlyBudget)

            "hide loader" {
                isLoading.test().assertValue(false)
            }

            "update monthly budget" {
                monthlyBudget.test().assertValue(loadedMonthlyBudget)
            }

            "on refresh" - {
                actions.offer(MainAction.Refresh)

                "get refreshed monthly budget for current month" {
                    api.getMonthlyBudget.invocations.run {
                        count() shouldBe 2
                        last() shouldBe Month.APRIL
                    }
                }
            }

            "on open spreadsheet" - {
                actions.offer(MainAction.OpenSpreadsheet)

                "launch spreadsheet" {
                    verify(spreadsheetLauncher).launch()
                }
            }

            "on select prev month" - {
                actions.offer(MainAction.SelectPrevMonth)

                "get monthly budget for previous month" {
                    api.getMonthlyBudget.invocations.last() shouldBe Month.MARCH
                }
            }

            "on select next month" - {
                actions.offer(MainAction.SelectNextMonth)

                "get monthly budget for next month" {
                    api.getMonthlyBudget.invocations.last() shouldBe Month.MAY
                }
            }
        }
    }
})
