package com.example.simonkye_expenseapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.simonkye_expenseapp.databinding.FragmentExpenseDetailBinding
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
private const val TAG = "ExpenseDetailFragment"
class ExpenseDetailFragment : Fragment() {
    private var _binding: FragmentExpenseDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: ExpenseDetailFragmentArgs by navArgs()
    private val expenseDetailViewModel : ExpenseDetailViewModel by viewModels {
        ExpenseDetailViewModelFactory(args.expenseId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentExpenseDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            expenseName.doOnTextChanged { text, _, _, _ ->
                expenseDetailViewModel.updateExpense { oldExpense ->
                    oldExpense.copy(name = text.toString())
                }
            }

            expenseAmount.doOnTextChanged { text, _, _, _ ->
                expenseDetailViewModel.updateExpense { oldExpense ->
                    val amount = try {
                        text.toString().toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                    oldExpense.copy(amount = amount)
                }
            }
            expenseCategories.setOnCheckedChangeListener { _, checkedId ->
                expenseDetailViewModel.updateExpense { oldExpense ->
                    val radioButton = view.findViewById<RadioButton>(checkedId)
                    oldExpense.copy(category = radioButton?.text.toString())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseDetailViewModel.expense.collect { expense ->
                    expense?.let { updateUi(it) }

                }
            }
        }
        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { requestKey, bundle ->
            val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            expenseDetailViewModel.updateExpense { it.copy(date = newDate) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(expense: Expense) {
        binding.apply {
            if (expenseName.text.toString() != expense.name) {
                expenseName.setText(expense.name)
            }
            expenseDate.text = expense.date.toString()
            expenseDate.setOnClickListener {
                findNavController().navigate(
                    ExpenseDetailFragmentDirections.selectDate(expense.date)
                )
            }
            if (expenseAmount.text.toString() != expense.amount.toString()) {
                expenseAmount.setText(expense.amount.toString())
            }
            val radioGroup: RadioGroup = expenseCategories
            for (i in 0 until radioGroup.childCount) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                if (radioButton.text == expense.category) {
                    radioButton.isChecked = true
                    break
                }
            }
        }
    }
}