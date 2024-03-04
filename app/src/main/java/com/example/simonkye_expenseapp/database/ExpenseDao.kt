package com.example.simonkye_expenseapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.simonkye_expenseapp.Expense
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.UUID

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense")
    fun getExpenses() : Flow<List<Expense>>

    @Query("SELECT * FROM expense WHERE id=(:id)")
    suspend fun getExpense(id: UUID) : Expense

    @Query("SELECT * FROM expense WHERE category=(:category)")
    fun getExpense(category: String) : Flow<List<Expense>>

    @Query("SELECT * FROM expense WHERE date=(:date)")
    fun getExpense(date: Date) : Flow<List<Expense>>

    @Query("SELECT * FROM expense WHERE date=(:date) AND category=(:category)")
    fun getExpense(date: Date, category: String) : Flow<List<Expense>>

    @Update
    suspend fun updateExpense(expense: Expense)

    @Insert
    suspend fun addExpense(expense: Expense)
}