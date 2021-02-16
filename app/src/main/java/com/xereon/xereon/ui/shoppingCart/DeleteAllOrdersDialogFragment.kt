package com.xereon.xereon.ui.shoppingCart

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.xereon.xereon.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllOrdersDialogFragment : DialogFragment() {

    private val viewModel by viewModels<DeleteAllOrdersViewModel>()
    //private val args by navArgs<DeleteAllOrdersDialogFragmentArgs>()

    private var storeId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //storeId = args.storeId
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Alle Produkte entfernen")
            .setMessage("Alle Produkte von diesem Unternehmen werden unwiederruflich gelöscht.")
            .setNegativeButton("Abbrechen", null)
            .setPositiveButton("Löschen") {_, _ ->
                viewModel.onConfirmClick(storeId)
            }
            .create()
}