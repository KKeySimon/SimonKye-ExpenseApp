package com.example.simonkye_expenseapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simonkye_expenseapp.databinding.FragmentExpenseListBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

private const val TAG = "ExpenseListFragment"
class ExpenseListFragment : Fragment() {
    private var _binding: FragmentExpenseListBinding ?= null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val expenseListViewModel : ExpenseListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_expense -> {
                showNewExpense()
                true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showNewExpense() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newExpense = Expense(
                id = UUID.randomUUID(),
                name = "",
                date = Date(),
                amount = 0,
                category = ""
            )
            expenseListViewModel.addExpense(newExpense)
            findNavController().navigate(
                ExpenseListFragmentDirections.showExpenseDetail(newExpense.id)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)

        binding.expenseRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            val selectedDate = it.getString("date", "No Date Selected")
            binding.expenseDate.text = selectedDate
        }
        binding.categories.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = view.findViewById<RadioButton>(checkedId)
            val radioButtonText = radioButton?.text.toString()
            val dateButtonText = binding.expenseDate.toString()
            viewLifecycleOwner.lifecycleScope.launch {
//                if (dateButtonText == getString(R.string.no_date_filter)) {
//
//                }
                if (radioButtonText == "All") {
                    expenseListViewModel.loadExpense()
                } else {
                    expenseListViewModel.loadExpenses(radioButtonText)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseListViewModel.expenses.collect { expenses ->
                    binding.expenseRecyclerView.adapter =
                        ExpenseListAdapter(expenses) { expenseId ->
                            findNavController().navigate(
                                ExpenseListFragmentDirections.showExpenseDetail(expenseId)
                            )
                        }
                    binding.apply {
                        expenseDate.setOnClickListener {
                            findNavController().navigate(
                                ExpenseListFragmentDirections.filterDate(Date())
                            )
                        }
                    }
                    setFragmentResultListener(
                        DatePickerFragment.REQUEST_KEY_DATE
                    ) { requestKey, bundle ->
                        val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
                        binding.apply {
                            expenseDate.text = newDate.toString()
                        }
                        viewLifecycleOwner.lifecycleScope.launch {
                            expenseListViewModel.loadExpenses(newDate)
                        }
                    }
                }
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (_binding != null) {
            outState.putString("date", binding.expenseDate.text.toString())
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_expense_list, menu)
    }
}