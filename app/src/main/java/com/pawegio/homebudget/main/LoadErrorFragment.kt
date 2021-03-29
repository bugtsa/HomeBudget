package com.pawegio.homebudget.main

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.pawegio.homebudget.R
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import splitties.alertdialog.appcompat.*

class LoadErrorFragment : DialogFragment() {

    //TODO: extract to new view model
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        requireContext().alertDialog {
            titleResource = R.string.load_error_title
            messageResource = R.string.load_error_message
            positiveButton(R.string.try_again) {
                dismissAllowingStateLoss()
                viewModel.actions.accept(MainAction.TryAgain)
            }
            negativeButton(R.string.pick_document) {
                dismissAllowingStateLoss()
                viewModel.actions.accept(MainAction.PickDocumentAgain)
            }
        }
}
