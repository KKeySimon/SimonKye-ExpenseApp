package com.example.simonkye_expenseapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
private const val TAG = "ExpenseListViewModel"
class ExpenseListViewModel : ViewModel() {
    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> = _selectedDate

    private val _selectedCategoryId = MutableLiveData<Int>()
    val selectedCategoryId: LiveData<Int> = _selectedCategoryId

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun setSelectedCategoryId(id: Int) {
        _selectedCategoryId.value = id
    }
    private val expenseRepository = ExpenseRepository.get()

    private val _expenses: MutableStateFlow<List<Expense>> = MutableStateFlow(emptyList())
    val expenses: StateFlow<List<Expense>>
        get() = _expenses.asStateFlow()

    val categories = arrayOf<String>("Food", "Entertainment", "Housing", "Utilities", "Fuel", "Automotive", "Misc")
    init {
        viewModelScope.launch {
            expenseRepository.getExpenses().collect {
                _expenses.value = it
            }
        }
    }
    suspend fun addExpense(expense: Expense) {
        expenseRepository.addExpense(expense)
    }
    suspend fun loadExpenses(category : String) {
        expenseRepository.getExpenseByCategory(category).collect {
            _expenses.value = it
        }
    }

    suspend fun loadExpenses(date : Date) {
        expenseRepository.getExpenseByDate(date).collect {
            _expenses.value = it
        }
    }

    suspend fun loadExpenses(date : Date, category: String) {
        expenseRepository.getExpenseByDateAndCategory(date, category).collect {
            _expenses.value = it
        }
    }

    suspend fun loadExpense() {
        expenseRepository.getExpenses().collect {
            _expenses.value = it
        }
    }
}