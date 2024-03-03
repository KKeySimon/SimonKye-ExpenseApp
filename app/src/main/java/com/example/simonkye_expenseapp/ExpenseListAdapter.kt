package com.example.simonkye_expenseapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.simonkye_expenseapp.databinding.ListItemExpenseBinding
import java.util.UUID

class ExpenseHolder (
    private val binding: ListItemExpenseBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(expense: Expense, onExpenseClicked: (expenseId: UUID) -> Unit) {
        binding.expenseName.text = expense.name
        binding.expenseDate.text = expense.date.toString()
        binding.expenseCategory.text = expense.category

        binding.root.setOnClickListener {
            onExpenseClicked(expense.id)
        }
    }
}

class ExpenseListAdapter (
    private val expenses: List<Expense>,
    private val onExpenseClicked: (expenseId: UUID) -> Unit
) : RecyclerView.Adapter<ExpenseHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemExpenseBinding.inflate(inflater, parent, false)
        return ExpenseHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseHolder, position: Int) {
        val expense = expenses[position]
        holder.bind(expense, onExpenseClicked)
    }

    override fun getItemCount(): Int {
        return expenses.size
    }
}