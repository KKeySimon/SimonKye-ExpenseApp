package com.example.simonkye_expenseapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.simonkye_expenseapp.Expense
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense")
    fun getExpenses() : Flow<List<Expense>>

    @Query("SELECT * FROM expense WHERE id=(:id)")
    suspend fun getExpense(id: UUID) : Expense

    @Query("SELECT * FROM expense WHERE category=(:category)")
    fun getExpense(category: String) : Flow<List<Expense>>

    @Update
    suspend fun updateExpense(expense: Expense)

    @Insert
    suspend fun addExpense(expense: Expense)
}